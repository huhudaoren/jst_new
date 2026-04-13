package com.ruoyi.jst.common.cache;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 竞赛通热点数据缓存服务。
 * <p>
 * 封装若依 {@link RedisCache}，提供统一的缓存读写接口：
 * <ul>
 *   <li>{@link #getOrLoad} - 非分页查询缓存（含空值防穿透 + TTL 抖动防雪崩）</li>
 *   <li>{@link #getOrLoadPage} - 分页查询缓存（保留 PageHelper 的 total/pageNum/pageSize）</li>
 *   <li>{@link #evict} - 删除单个缓存 key</li>
 *   <li>{@link #evictByPrefix} - 按前缀批量清除缓存</li>
 * </ul>
 * <p>
 * 缓存 Key 前缀统一使用 {@code cache:} 命名空间，与分布式锁 {@code lock:} 区分。
 * 完整规约见 {@code 架构设计/15-Redis-Key与锁规约.md}。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstCacheService {

    private static final Logger log = LoggerFactory.getLogger(JstCacheService.class);

    /** 空值占位符，用于缓存穿透防护 */
    private static final String NULL_SENTINEL = "CACHE_NULL_SENTINEL";

    /** 空值缓存 TTL（秒） */
    private static final int NULL_TTL_SECONDS = 60;

    /** 分页缓存 Map 中的字段名 */
    private static final String PAGE_KEY_ROWS = "rows";
    private static final String PAGE_KEY_TOTAL = "total";
    private static final String PAGE_KEY_PAGE_NUM = "pageNum";
    private static final String PAGE_KEY_PAGE_SIZE = "pageSize";

    @Autowired
    private RedisCache redisCache;

    /**
     * 查缓存，未命中则执行 loader 并写入缓存。
     * <p>
     * 防穿透：loader 返回 null 时缓存 NULL_SENTINEL 标记 60s，避免重复查库。
     * 防雪崩：实际 TTL = baseTtl +/- 20% 随机抖动。
     * <p>
     * 注意：缓存命中时会自动清除 PageHelper ThreadLocal，
     * 防止 Controller 层 startPage() 的残留状态影响后续查询。
     *
     * @param key        缓存 key（建议以 cache: 开头）
     * @param ttlSeconds 基础 TTL（秒）
     * @param loader     数据加载器（缓存未命中时执行）
     * @param <T>        数据类型
     * @return 缓存或加载的数据；不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(String key, int ttlSeconds, Supplier<T> loader) {
        Object cached = redisCache.getCacheObject(key);
        if (cached != null) {
            // 安全清理：防止 Controller 层 startPage() 残留导致后续查询被分页
            PageHelper.clearPage();
            if (NULL_SENTINEL.equals(cached)) {
                return null;
            }
            log.debug("[JstCache] HIT key={}", key);
            return (T) cached;
        }

        T data = loader.get();
        if (data != null) {
            int jitteredTtl = applyJitter(ttlSeconds);
            redisCache.setCacheObject(key, data, jitteredTtl, TimeUnit.SECONDS);
            log.debug("[JstCache] MISS -> loaded key={} ttl={}s", key, jitteredTtl);
        } else {
            // 空值缓存防穿透
            redisCache.setCacheObject(key, NULL_SENTINEL, NULL_TTL_SECONDS, TimeUnit.SECONDS);
            log.debug("[JstCache] MISS -> null sentinel key={} ttl={}s", key, NULL_TTL_SECONDS);
        }
        return data;
    }

    /**
     * 分页查询缓存。
     * <p>
     * 与 {@link #getOrLoad} 不同之处：
     * <ul>
     *   <li>缓存时将 PageHelper {@link Page} 的 total/pageNum/pageSize 一起存入 Map，
     *       避免 FastJson2 autoType 白名单不包含 PageHelper 导致反序列化失败</li>
     *   <li>缓存命中时重建 {@link Page} 对象，使 Controller 的
     *       {@code getDataTable()} 能正确读取 total</li>
     *   <li>缓存命中时清除 PageHelper ThreadLocal</li>
     * </ul>
     *
     * @param key        缓存 key
     * @param ttlSeconds 基础 TTL（秒）
     * @param loader     数据加载器（缓存未命中时执行，应返回 PageHelper 拦截后的 Page 列表）
     * @param <T>        列表元素类型
     * @return 缓存或加载的分页列表
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getOrLoadPage(String key, int ttlSeconds, Supplier<List<T>> loader) {
        Object cached = redisCache.getCacheObject(key);
        if (cached != null) {
            PageHelper.clearPage();
            if (NULL_SENTINEL.equals(cached)) {
                return Collections.emptyList();
            }
            // 从缓存 Map 重建 Page 对象
            if (cached instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) cached;
                List<T> rows = (List<T>) map.get(PAGE_KEY_ROWS);
                long total = toLong(map.get(PAGE_KEY_TOTAL), 0L);
                int pn = toInt(map.get(PAGE_KEY_PAGE_NUM), 1);
                int ps = toInt(map.get(PAGE_KEY_PAGE_SIZE), 10);

                Page<T> page = new Page<>(pn, ps, false);
                page.addAll(rows != null ? rows : Collections.emptyList());
                page.setTotal(total);
                log.debug("[JstCache] PAGE HIT key={} total={}", key, total);
                return page;
            }
            return (List<T>) cached;
        }

        // 缓存未命中 - 执行 loader（PageHelper 拦截 MyBatis 查询）
        List<T> data = loader.get();
        if (data != null && !data.isEmpty()) {
            Map<String, Object> cacheData = new LinkedHashMap<>();
            cacheData.put(PAGE_KEY_ROWS, new ArrayList<>(data));
            if (data instanceof Page) {
                Page<?> pageResult = (Page<?>) data;
                cacheData.put(PAGE_KEY_TOTAL, pageResult.getTotal());
                cacheData.put(PAGE_KEY_PAGE_NUM, pageResult.getPageNum());
                cacheData.put(PAGE_KEY_PAGE_SIZE, pageResult.getPageSize());
            } else {
                cacheData.put(PAGE_KEY_TOTAL, (long) data.size());
                cacheData.put(PAGE_KEY_PAGE_NUM, 1);
                cacheData.put(PAGE_KEY_PAGE_SIZE, data.size());
            }
            int jitteredTtl = applyJitter(ttlSeconds);
            redisCache.setCacheObject(key, cacheData, jitteredTtl, TimeUnit.SECONDS);
            log.debug("[JstCache] PAGE MISS -> loaded key={} ttl={}s total={}", key, jitteredTtl, cacheData.get(PAGE_KEY_TOTAL));
        } else {
            redisCache.setCacheObject(key, NULL_SENTINEL, NULL_TTL_SECONDS, TimeUnit.SECONDS);
            log.debug("[JstCache] PAGE MISS -> empty key={}", key);
        }
        return data;
    }

    /**
     * 删除单个缓存 key。
     *
     * @param key 缓存 key
     */
    public void evict(String key) {
        redisCache.deleteObject(key);
        log.debug("[JstCache] evict key={}", key);
    }

    /**
     * 按前缀批量清除缓存。
     * <p>
     * 使用 Redis {@code KEYS prefix*} 匹配后批量删除。
     * 仅限于缓存 key（cache: 前缀），key 数量可控。
     *
     * @param prefix key 前缀（不含末尾 *）
     */
    public void evictByPrefix(String prefix) {
        Collection<String> keys = redisCache.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
            log.debug("[JstCache] evictByPrefix prefix={} count={}", prefix, keys.size());
        }
    }

    /**
     * TTL 抖动：+/- 20% 随机偏移，防止大量 key 同时过期导致雪崩。
     *
     * @param baseTtl 基础 TTL（秒）
     * @return 加抖动后的 TTL
     */
    private int applyJitter(int baseTtl) {
        if (baseTtl <= 0) {
            return 1;
        }
        int delta = baseTtl / 5;
        if (delta <= 0) {
            return baseTtl;
        }
        int jitter = ThreadLocalRandom.current().nextInt(-delta, delta + 1);
        return Math.max(1, baseTtl + jitter);
    }

    private long toLong(Object obj, long defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        return defaultValue;
    }

    private int toInt(Object obj, int defaultValue) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return defaultValue;
    }
}
