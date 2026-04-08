package com.ruoyi.jst.common.lock;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁统一模板
 * <p>
 * 强制所有业务代码使用本模板加锁，禁止裸用 RedissonClient.getLock(...)。
 * 锁名必须先在 15-Redis-Key与锁规约.md 中登记。
 * <p>
 * 使用示例：
 * <pre>
 * return jstLockTemplate.execute("lock:team_appt:" + teamId, 5, 10, () -&gt; {
 *     // 状态机校验 + 扣减 + 写库（不要做 RPC/IO）
 *     return doWriteoff(teamId, qty);
 * });
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class JstLockTemplate {

    private static final Logger log = LoggerFactory.getLogger(JstLockTemplate.class);

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁执行业务
     *
     * @param lockKey  锁名（必须以 lock: 开头并已在 15-Redis-Key与锁规约.md 登记）
     * @param waitSec  最大等待秒数
     * @param leaseSec 持有秒数（看门狗会续期，但请按业务最长执行时间设置兜底值）
     * @param action   业务逻辑
     * @param <T>      返回值类型
     * @return action.get() 返回值
     * @throws ServiceException 加锁失败时
     */
    public <T> T execute(String lockKey, long waitSec, long leaseSec, Supplier<T> action) {
        if (lockKey == null || !lockKey.startsWith("lock:")) {
            throw new IllegalArgumentException("锁名必须以 lock: 开头：" + lockKey);
        }
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        long start = System.currentTimeMillis();
        try {
            locked = lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("[JstLock] 获取锁失败 key={} wait={}s", lockKey, waitSec);
                throw new ServiceException(BizErrorCode.JST_COMMON_LOCK_TIMEOUT.message(),
                        BizErrorCode.JST_COMMON_LOCK_TIMEOUT.code());
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[JstLock] 加锁被中断 key={}", lockKey, e);
            throw new ServiceException("加锁被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("[JstLock] 释放锁异常 key={}", lockKey, e);
                }
            }
            long cost = System.currentTimeMillis() - start;
            if (cost > 1000) {
                log.warn("[JstLock] 慢锁告警 key={} cost={}ms", lockKey, cost);
            }
        }
    }

    /**
     * 无返回值的执行重载
     */
    public void execute(String lockKey, long waitSec, long leaseSec, Runnable action) {
        execute(lockKey, waitSec, leaseSec, () -> {
            action.run();
            return null;
        });
    }
}
