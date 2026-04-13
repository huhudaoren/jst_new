# PERF-CACHE — 热点数据缓存层

> 优先级：P1 | 预估：M | Agent：Backend Agent

---

## 一、背景

当前系统热点查询（赛事列表/详情、课程列表、首页推荐等）每次都直接查数据库。在报名高峰期（3~5 万人/天），这些只读查询会给 MySQL 带来不必要的压力。需要在 Service 层引入 Redis 缓存。

## 二、缓存策略

### 2.1 需要缓存的热点数据

| # | 数据 | 当前 QPS | 缓存 TTL | 缓存 Key 模式 | 失效时机 |
|---|---|---|---|---|---|
| 1 | 赛事列表（首页推荐） | 高 | 5 min | `cache:contest:list:{category}:{page}` | 赛事上线/下线时清除 |
| 2 | 赛事详情 | 高 | 10 min | `cache:contest:detail:{contestId}` | 赛事编辑时清除 |
| 3 | 课程列表 | 中 | 5 min | `cache:course:list:{type}:{page}` | 课程上下线时清除 |
| 4 | 课程详情 | 中 | 10 min | `cache:course:detail:{courseId}` | 课程编辑时清除 |
| 5 | 首页数据（banner/推荐/标签） | 高 | 3 min | `cache:home:{section}` | 后台更新时清除 |
| 6 | 公告列表 | 中 | 5 min | `cache:notice:list:{page}` | 公告发布/下线时清除 |
| 7 | 字典数据 | 中 | 30 min | `cache:dict:{dictType}` | 字典更新时清除 |
| 8 | 赛事分类列表 | 低 | 60 min | `cache:contest:categories` | 分类变更时清除 |

### 2.2 不需要缓存的数据

| 数据 | 原因 |
|---|---|
| 订单列表/详情 | 用户私有数据，缓存命中率低 |
| 报名记录 | 状态频繁变化 |
| 积分余额 | 实时性要求高 |
| 支付/退款 | 资金敏感，必须查库 |

## 三、实现方案

### 3.1 缓存工具类

新建 `jst-common/src/main/java/com/ruoyi/jst/common/cache/JstCacheService.java`：

```java
@Service
public class JstCacheService {
    @Autowired
    private RedisCache redisCache;
    
    /**
     * 查缓存，未命中则执行 loader 并写入缓存
     */
    public <T> T getOrLoad(String key, int ttlSeconds, Supplier<T> loader) {
        T cached = redisCache.getCacheObject(key);
        if (cached != null) return cached;
        
        T data = loader.get();
        if (data != null) {
            redisCache.setCacheObject(key, data, ttlSeconds, TimeUnit.SECONDS);
        } else {
            // 空值缓存防穿透（短 TTL）
            redisCache.setCacheObject(key, "NULL", 60, TimeUnit.SECONDS);
        }
        return data;
    }
    
    /**
     * 按前缀清除缓存
     */
    public void evictByPrefix(String prefix) {
        Collection<String> keys = redisCache.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
    }
}
```

### 3.2 Service 层接入示例

以赛事列表为例，在 `WxContestController` 或 Service 中：

```java
// 旧：直接查库
public List<ContestVO> wxList(ContestQueryDTO query) {
    return contestMapper.selectWxList(query);
}

// 新：走缓存
public List<ContestVO> wxList(ContestQueryDTO query) {
    String key = "cache:contest:list:" + query.getCategory() + ":" + query.getPageNum();
    return jstCacheService.getOrLoad(key, 300, () -> contestMapper.selectWxList(query));
}
```

### 3.3 缓存失效（写操作后清除）

在赛事/课程/公告的增删改 Service 方法中加清除逻辑：

```java
// ContestServiceImpl.updateContest() 末尾
jstCacheService.evictByPrefix("cache:contest:");

// CourseServiceImpl.updateCourse() 末尾
jstCacheService.evictByPrefix("cache:course:");

// NoticeServiceImpl.publishNotice() 末尾
jstCacheService.evictByPrefix("cache:notice:");
```

### 3.4 缓存穿透防护

`getOrLoad` 已内置空值缓存（TTL=60s）。对于不存在的 ID 查询，缓存 "NULL" 标记，60 秒内不再查库。

### 3.5 缓存雪崩防护

TTL 加随机抖动（±20%）：
```java
int jitter = ThreadLocalRandom.current().nextInt(-ttlSeconds / 5, ttlSeconds / 5);
redisCache.setCacheObject(key, data, ttlSeconds + jitter, TimeUnit.SECONDS);
```

## 四、Redis Key 规约更新

更新 `架构设计/15-Redis-Key与锁规约.md`，补充缓存 key 规范：

| 前缀 | 用途 | TTL | 示例 |
|---|---|---|---|
| `lock:` | 分布式锁 | 5-60s | `lock:order:create:{userId}` |
| `cache:contest:` | 赛事缓存 | 5-10 min | `cache:contest:detail:94701` |
| `cache:course:` | 课程缓存 | 5-10 min | `cache:course:list:normal:1` |
| `cache:home:` | 首页数据 | 3 min | `cache:home:recommend-contests` |
| `cache:notice:` | 公告缓存 | 5 min | `cache:notice:list:1` |
| `cache:dict:` | 字典缓存 | 30 min | `cache:dict:contest_category` |

## 五、DoD

- [ ] JstCacheService 工具类实现（含空值防穿透 + TTL 抖动防雪崩）
- [ ] 8 个热点查询接入缓存
- [ ] 写操作后缓存清除
- [ ] Redis Key 规约文档更新
- [ ] `mvn compile -DskipTests` 通过
- [ ] 启动验证缓存命中（log 或 Redis CLI 确认 key 存在）
- [ ] 报告交付
