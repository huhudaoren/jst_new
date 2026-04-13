# 任务报告 - PERF-CACHE 热点数据缓存层

## A. 任务前自检（Step 2 答题）

1. **涉及表**：无直接表操作，缓存层覆盖 jst_contest / jst_course / jst_notice / sys_dict_data 的只读查询
2. **涉及状态机**：无
3. **涉及权限标识**：无（不新增 Controller，只改 Service 层）
4. **涉及锁名**：无（只读缓存，无分布式锁）
5. **事务边界**：无（缓存操作不涉及事务）
6. **ResVO 字段**：无新增（复用已有 VO）
7. **复用样板**：`jst-user/.../ParticipantClaimServiceImpl.java`（Service 结构）+ `NoticeServiceImpl`（已有 dict 缓存模式）

## B. 交付物清单

**新增文件：**
- `jst-common/src/main/java/com/ruoyi/jst/common/cache/JstCacheService.java`
  — 含 `getOrLoad`（非分页）/ `getOrLoadPage`（分页 + PageHelper 兼容）/ `evict` / `evictByPrefix`
  — 空值防穿透（NULL sentinel 60s）+ TTL +/-20% 抖动防雪崩

**修改文件：**
- `jst-event/.../ContestServiceImpl.java` — 缓存 selectWxList / getWxDetail / selectHotList / selectCategoryStats + 8 个写方法加 evict
- `jst-event/.../CourseServiceImpl.java` — 缓存 selectWxList / getWxDetail + 7 个写方法加 evict
- `jst-event/.../HomeServiceImpl.java` — 缓存 selectRecommendContests / selectRecommendCourses / selectHotTags / selectTopics
- `jst-message/.../NoticeServiceImpl.java` — 缓存 selectWxNoticeList / selectBannerList + 字典缓存迁移到 JstCacheService + 4 个写方法加 evict
- `架构设计/15-Redis-Key与锁规约.md` — 补充 section 2.2 热点数据缓存 Key 清单（13 条 cache: 前缀 key）

## C. mvn compile 结果

```
[INFO] BUILD SUCCESS
[INFO] Total time: 39.032 s
```

18 模块全部 SUCCESS。

## D. 缓存接入统计

| # | 缓存点 | Key 模式 | TTL | 方式 |
|---|---|---|---|---|
| 1 | 赛事列表 | `cache:contest:list:{cat}:{pn}` | 5min | getOrLoadPage（分页） |
| 2 | 赛事详情 | `cache:contest:detail:{id}` | 10min | getOrLoad |
| 3 | 热门赛事 | `cache:contest:hot:{limit}` | 5min | getOrLoad |
| 4 | 分类统计 | `cache:contest:categories` | 60min | getOrLoad |
| 5 | 课程列表 | `cache:course:list:{type}:{pn}` | 5min | getOrLoadPage（分页） |
| 6 | 课程详情 | `cache:course:detail:{id}` | 10min | getOrLoad |
| 7 | 首页推荐赛事 | `cache:home:recommend-contests` | 3min | getOrLoad |
| 8 | 首页推荐课程 | `cache:home:recommend-courses` | 3min | getOrLoad |
| 9 | 首页热门标签 | `cache:home:hot-tags` | 3min | getOrLoad |
| 10 | 首页专题 | `cache:home:topics` | 3min | getOrLoad |
| 11 | 首页 Banner | `cache:home:banners` | 3min | getOrLoad |
| 12 | 公告列表 | `cache:notice:list:{cat}:{pn}` | 5min | getOrLoad |
| 13 | 字典数据 | `cache:dict:{dictType}` | 30min | getOrLoad（迁移自直接 redisCache） |

**缓存清除覆盖：**

| Service | 触发方法 | 清除范围 |
|---|---|---|
| ContestServiceImpl | add/edit/submit/approve/reject/online/offline/delete | `cache:contest:*` + `cache:home:*` |
| CourseServiceImpl | add/edit/submit/approve/reject/on/off | `cache:course:*` + `cache:home:*` |
| NoticeServiceImpl | add/edit/publish/offline | `cache:notice:*` + `cache:home:*` |

## E. 遗留 TODO

- 赛事列表缓存中，关键词/标签/组别搜索跳过缓存（高基数低命中率，直接查库）
- `getOrLoadPage` 将 PageHelper 的 Page 对象转为 Map 存储再重建，解决了 FastJson2 autoType 白名单不含 `com.github.pagehelper.Page` 的序列化问题
- 字典缓存无主动清除机制（字典更新在若依系统模块，不可修改），依赖 30min TTL 自然过期

## F. 我做了任务卡之外的什么

- 在 JstCacheService 新增了 `getOrLoadPage` 方法（任务卡未列），用于正确处理分页查询的 PageHelper Page 对象序列化/反序列化 + total 保留
- `getOrLoad` 的缓存命中路径加了 `PageHelper.clearPage()` 安全清理，防止 Controller 层 `startPage()` 残留影响后续查询
- 字典缓存从 `jst:dict:` 前缀迁移到 `cache:dict:` 前缀，与新规约一致

## G. 自检清单确认 (16-安全文档 section 8)

- [x] 所有方法 @PreAuthorize — 未新增 Controller，不涉及
- [x] ReqDTO 有 JSR-303 — 未新增 DTO
- [x] 出参用 ResVO — 未修改出参类型
- [x] 敏感字段脱敏 — 缓存数据无敏感字段
- [x] 详情接口归属校验 — 缓存包裹原有校验逻辑，不绕过
- [x] 写操作 @OperateLog — 不涉及新写操作
- [x] 资金/状态机方法 @Transactional — 不涉及
- [x] 高并发方法 jstLockTemplate.execute — 不涉及
- [x] 没有 ${} 拼 SQL — 不涉及 SQL
- [x] 没有打印明文敏感字段 — 仅 debug 级别日志打印 cache key

## H. 技术要点说明

### JstCacheService 核心设计

1. **getOrLoad（非分页）**：标准 cache-aside 模式，缓存命中直接返回，未命中执行 loader 后写入缓存
2. **getOrLoadPage（分页）**：
   - 缓存时将 PageHelper 的 Page 对象拆解为 `Map{rows, total, pageNum, pageSize}` 存储
   - 原因：FastJson2 autoType 白名单仅允许 `com.ruoyi.*`，`com.github.pagehelper.Page` 不在白名单内，直接缓存 Page 对象反序列化会失败
   - 缓存命中时重建 `Page<T>` 对象并设置 total，使 Controller 的 `getDataTable()` -> `new PageInfo(list).getTotal()` 能正确返回总数
3. **PageHelper.clearPage()**：缓存命中时清除 PageHelper ThreadLocal，防止 Controller 层 `startPage()` 设置的分页参数未被消费，影响同一请求内后续 MyBatis 查询
4. **空值防穿透**：loader 返回 null 时缓存 `CACHE_NULL_SENTINEL` 字符串标记 60s
5. **TTL 抖动防雪崩**：`applyJitter(baseTtl)` 在 baseTtl 基础上随机偏移 +/-20%
