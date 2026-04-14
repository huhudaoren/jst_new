# 15. Redis Key 命名与 Redisson 锁规约

> 决策 Q1=A：使用 Redisson 实现所有分布式锁
> 强制：任何业务代码使用 Redis 必须先在本文档登记 key 与 TTL，禁止散落

---

## 1. 命名总规范

### 1.1 命名空间前缀
所有业务 key 一律 `jst:` 前缀，与若依原生区隔（若依用 `sys_*` 等无前缀格式）。

### 1.2 命名格式
```
jst:{domain}:{purpose}:{key1}[:{key2}]
```
- 段间用 `:` 分隔（Redis 业界惯例，便于 RDB tools 看树形）
- 不使用驼峰，使用小写下划线
- 业务 ID 直接用数字，不要再 hash

### 1.3 TTL 必须显式设置
**禁止**任何无 TTL 的 key（除少数计数器场景须由文档单独说明）。

---

## 2. 缓存 Key 清单

| Key 模板 | TTL | 类型 | 用途 | 写入位置 |
|---|---|---|---|---|
| `jst:user:profile:{userId}` | 30min | String/JSON | 用户基础信息缓存 | UserService.getProfile() |
| `jst:user:openid:{openid}` | 30min | String | openid → userId 反查 | WxAuthService |
| `jst:contest:detail:{contestId}` | 10min | String/JSON | 赛事详情缓存（含表单模板id） | ContestService.getDetail() |
| `jst:contest:list:hot` | 5min | List | 热门赛事列表 | ContestQueryService |
| `jst:form:template:{tplId}:v{version}` | 1h | String/JSON | 动态表单 schema 渲染缓存 | FormTemplateService |
| `jst:points:account:{ownerType}:{ownerId}` | 5min | Hash | 积分账户余额缓存 | PointsAccountService |
| `jst:rebate:rule:{contestId}:{channelId}` | 10min | String/JSON | 命中规则缓存 | RebateRuleService.match() |
| `jst:dict:{dictType}` | 1h | String/JSON | 业务字典缓存（订单状态/退款状态等） | DictCacheService |
| `jst:level:config:all` | 1h | String/JSON | 等级配置全表缓存 | LevelConfigService |
| `jst:cert:verify:{verifyCode}` | 1d | String/JSON | 公开证书验证缓存 | CertVerifyController |
| `jst:msg:template:{tplCode}` | 1h | String | 消息模板缓存 | MessageTemplateService |
| `jst:biz_no:{ruleCode}:{dateKey}` | 48h | String(Long) | 统一编号日序列计数器（Redis INCR） | BizNoGenerateService.nextNo |

### 2.2 热点数据缓存 Key 清单（cache: 前缀，由 JstCacheService 统一管理）

> PERF-CACHE 任务新增。这些 key 由 `jst-common` 的 `JstCacheService` 统一读写，
> 含空值防穿透（NULL 标记 60s）+ TTL 抖动防雪崩（±20%）。

| Key 模板 | 基础 TTL | 用途 | 写入位置 | 失效时机 |
|---|---|---|---|---|
| `cache:contest:list:{category}:{pageNum}` | 5min | 赛事列表分页缓存 | ContestServiceImpl.selectWxList | 赛事增删改/上下线 |
| `cache:contest:detail:{contestId}` | 10min | 赛事详情缓存 | ContestServiceImpl.getWxDetail | 赛事编辑/上下线 |
| `cache:contest:hot:{limit}` | 5min | 热门赛事列表 | ContestServiceImpl.selectHotList | 赛事上下线 |
| `cache:contest:categories` | 60min | 赛事分类统计 | ContestServiceImpl.selectCategoryStats | 赛事增删改 |
| `cache:course:list:{courseType}:{pageNum}` | 5min | 课程列表分页缓存 | CourseServiceImpl.selectWxList | 课程增删改/上下架 |
| `cache:course:detail:{courseId}` | 10min | 课程详情缓存 | CourseServiceImpl.getWxDetail | 课程编辑/上下架 |
| `cache:home:recommend-contests` | 3min | 首页推荐赛事 | HomeServiceImpl.selectRecommendContests | 赛事/公告变更 |
| `cache:home:recommend-courses` | 3min | 首页推荐课程 | HomeServiceImpl.selectRecommendCourses | 课程/赛事变更 |
| `cache:home:hot-tags` | 3min | 首页热门标签 | HomeServiceImpl.selectHotTags | 赛事变更 |
| `cache:home:topics` | 3min | 首页专题活动 | HomeServiceImpl.selectTopics | 公告变更 |
| `cache:home:banners` | 3min | 首页 Banner | NoticeServiceImpl.selectBannerList | 公告发布/下线 |
| `cache:notice:list:{category}:{pageNum}` | 5min | 公告列表分页缓存 | NoticeServiceImpl.selectWxNoticeList | 公告增删改/发布/下线 |
| `cache:dict:{dictType}` | 30min | 业务字典缓存 | NoticeServiceImpl.selectDictOptions | TTL 自然过期 |

**缓存失效策略**：
- 写操作后由各 Service 的 `evictXxxCache()` 按前缀批量清除
- `evictByPrefix("cache:contest:")` → 清除赛事列表+详情+热门+分类
- `evictByPrefix("cache:course:")` → 清除课程列表+详情
- `evictByPrefix("cache:notice:")` → 清除公告列表
- `evictByPrefix("cache:home:")` → 清除首页所有聚合数据
- 前缀级 KEYS 扫描可控（cache: 命名空间 key 数量有限），非全量 `KEYS *`

### 缓存失效统一约定
- 写入数据时 **必须** 主动 `del` 对应 key（而不是等 TTL 自然过期）
- 多 key 联动失效（如更新等级配置 → del `jst:level:config:all`）封装到 Service 层
- **禁止使用 keys * 类操作**（`cache:` 前缀的定向 KEYS 扫描属于可控例外）

---

## 3. Redisson 分布式锁清单 ⭐ 强制登记

| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包裹的业务 |
|---|---|---|---|
| `lock:order:create:{userId}` | 用户下单防重复提交 | wait 3s, lease 10s | OrderService.createMixedPay |
| `lock:team_appt:{teamAppointmentId}` ⭐ | 团队预约扫码核销/状态推进 | wait 5s, lease 10s | AppointmentService.teamWriteoff、Job J03 |
| `lock:bind:{userId}` | 学生-渠道绑定切换 | wait 3s, lease 5s | BindingService.switchChannel |
| `lock:mall:goods:stock:{goodsId}` | 商城兑换库存扣减 | wait 3s, lease 5s | MallExchangeService.apply |
| `lock:mall:exchange:{exchangeId}` | 兑换订单状态推进互斥 (C8) | wait 2s, lease 5s | MallExchangeService.mockPaySuccess/cancel/ship/complete |
| `lock:mall:aftersale:{exchangeId}` | 商城售后申请/审核/退款互斥 (C9) | wait 3s, lease 10s | MallAftersaleService.apply/approve/reject |
| `lock:points:freeze:{userId}` | 积分冻结/扣减/解冻互斥 (C8) | wait 3s, lease 5s | MallExchangeService.apply/mockPaySuccess/cancel |
| `lock:rebate:settle:{channelId}` ⭐ | 渠道提现单审批（负向台账抵扣） | wait 10s, lease 30s | RebateSettlementService.approve |
| `lock:channel:withdraw:apply:{channelId}` | 渠道方创建提现申请 | wait 3s, lease 10s | ChannelWithdrawService.apply |
| `lock:channel:withdraw:cancel:{settlementId}` | 渠道方取消 pending 提现单 | wait 3s, lease 5s | ChannelWithdrawService.cancel |
| `lock:channel:withdraw:audit:{settlementId}` | admin 审核提现单（approve / reject） | wait 3s, lease 10s | ChannelWithdrawAdminService.approve/reject |
| `lock:channel:withdraw:execute:{settlementId}` | admin 执行提现打款互斥 | wait 3s, lease 15s | ChannelWithdrawAdminService.execute |
| `lock:channel:withdraw:offset:{channelId}` | 同渠道多张提现单串行化负向抵扣 | wait 5s, lease 10s | ChannelWithdrawAdminService.approve |
| `lock:event:settle:{partnerId}:{contestId}` | 赛事方结算单生成/审批 | wait 10s, lease 30s | EventSettlementService |
| `lock:points:adjust:{ownerType}:{ownerId}` | 积分账户人工调整 | wait 3s, lease 5s | PointsAdjustService（与 version 乐观锁配合） |
| `lock:claim:participant:{participantId}` | 临时档案认领 | wait 3s, lease 5s | ParticipantClaimService |
| `lock:job:{jobBeanName}` | 定时任务集群重入保护 | wait 0s, lease 60s | 全部 Quartz Job 入口 |
| `lock:wxpay:notify:{outTradeNo}` | 微信支付回调防重 | wait 5s, lease 10s | WxPayNotifyController |
| `lock:refund:{refundNo}` | 退款审批/执行 | wait 5s, lease 30s | RefundService.approve/execute |
| `lock:contest:audit:{contestId}` | 赛事审核状态推进 (F7) | wait 3s, lease 5s | ContestService.approve/reject/online |
| `lock:contest:status:{contestId}` | 赛事业务状态推进 (F7) | wait 3s, lease 5s | ContestService.transitBizStatus |
| `lock:form:template:save:{templateId}` | 动态表单模板保存(版本+1) (F8) | wait 3s, lease 5s | FormTemplateService.save |
| `lock:org:apply:{applyId}` | 赛事方入驻审核 (F5) | wait 3s, lease 10s | PartnerApplyService.approve/reject |
| `lock:org:channelApply:{applyId}` | 渠道认证审核 (F6) | wait 3s, lease 10s | ChannelAuthApplyService.approve |
| `lock:enroll:submit:{userId}:{contestId}` | 报名提交 (F9) | wait 3s, lease 5s | EnrollRecordService.submit |
| `lock:enroll:team:{userId}:{contestId}` | 团队报名并发锁 (REFACTOR-TODO-P2P3) | wait 5s, lease 10s | EnrollRecordService.submitTeam |
| `lock:enroll:audit:{enrollId}` | 报名审核 (F9) | wait 3s, lease 5s | EnrollRecordService.audit |
| `lock:enroll:create-order:{enrollId}` | 报名→订单 1:1 防重 (C2) | wait 3s, lease 10s | OrderService.createMixedPay |
| `lock:order:cancel:{orderId}` | 订单取消并发保护 (C2) | wait 3s, lease 5s | OrderService.cancel |

### 锁使用规范

```java
// 强制使用工具类，禁止散写 RLock
@Component
public class JstLockTemplate {
    @Autowired private RedissonClient redisson;

    /**
     * 加锁执行业务，超时抛 ServiceException
     * @param lockKey lock:xxx:{id}
     * @param waitSec 等待秒数
     * @param leaseSec 持有秒数（看门狗续期会自动延长，但需兜底）
     */
    public <T> T execute(String lockKey, long waitSec, long leaseSec, Supplier<T> action) {
        RLock lock = redisson.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS);
            if (!locked) {
                throw new ServiceException("操作过于频繁，请稍后再试 [" + lockKey + "]");
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceException("加锁被中断");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}
```

```java
// 业务调用
return jstLockTemplate.execute("lock:team_appt:" + teamId, 5, 10, () -> {
    // 状态机校验 + 扣减 + 写库
    return doWriteoff(teamId, qty);
});
```

### 强制约束
1. **禁止裸写** `redissonClient.getLock(...)`，必须用 `JstLockTemplate.execute()`
2. **禁止跨 Service 嵌套加锁**（防止死锁），如确需，按 lockKey 字典序加锁
3. **锁的粒度必须最细**：到 ID 级别，禁止全表锁
4. **锁内禁止 RPC / IO**：仅做内存计算 + DB 操作

---

## 4. 限流 / 计数 Key（业务侧使用）

| Key 模板 | TTL | 用途 |
|---|---|---|
| `jst:rate:enroll:{userId}:{date}` | 86400s | 用户单日报名次数限流 |
| `jst:rate:bind:{userId}:{date}` | 86400s | 单日切换绑定次数限流（风控） |
| `jst:rate:refund:{userId}:{date}` | 86400s | 单日退款次数限流 |
| `jst:counter:contest:view:{contestId}` | 1d | 赛事浏览数（异步落库） |

---

## 5. 禁止事项
- ❌ 不允许在缓存里存敏感字段（id_card_no、bank_account_no 等）
- ❌ 不允许使用 `KEYS *` / `FLUSHDB` 类阻塞命令
- ❌ 不允许把缓存当唯一数据源（必须有 DB 兜底）
- ❌ 不允许跨业务模块共用 key（每个模块自管前缀的子段）
# C6 补充登记

> 本节为 C6 团队预约与扫码核销新增锁，若与正文历史描述冲突，以本节为准。

| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包裹的业务 |
|---|---|---|---|
| `lock:team_appt:{teamAppointmentId}` | 团队预约到场核销人数累加 / 团队状态推进 | wait 3s, lease 5s | `WriteoffService.scan` |
| `lock:writeoff:item:{itemId}` | 单个核销子项互斥，防止同一码重复扫码 | wait 2s, lease 3s | `WriteoffService.scan` |
| `lock:appointment:book:{contestId}:{sessionCode}` | 场次名额扣减 / 团队预约防重入 | wait 3s, lease 5s | `TeamAppointmentService.apply` |
## C7 补充登记
| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包装的业务 |
|---|---|---|---|
| `lock:appointment:cancel:{appointmentId}` | 个人预约取消，防止同一预约并发回滚 | wait 2s, lease 5s | `AppointmentService.cancelIndividual` |

## F-MARKETING-RIGHTS-BE 补充登记
| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包装的业务 |
|---|---|---|---|
| `lock:coupon:claim:{userId}:{templateId}` | 用户主动领券，防止同一人并发重复领取 `once` 券 | wait 3s, lease 10s | `CouponUserService.claim` |
| `lock:rights:writeoff:{userRightsId}` | 权益自助核销互斥，防止同一权益并发扣减额度 | wait 3s, lease 10s | `RightsUserService.applyWriteoff` |

## E0-1 补充登记
| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包装的业务 |
|---|---|---|---|
| `lock:channel:unbind:{bindingId}` | 渠道方自助解绑学生，防止并发解绑同一绑定关系 | wait 2s, lease 5s | `ChannelSupplementService.unbindStudent` |
| `lock:org:channelApply:{applyId}` | 渠道认证申请重提/撤回互斥 | wait 3s, lease 5s | `ChannelAuthApplyService.resubmit/cancelApply` |

## F-USER-ADDRESS 补充登记
| 锁名模板 | 使用场景 | 等待/持有时长 | 必须包装的业务 |
|---|---|---|---|
| `lock:user:address:default:{userId}` | 设置默认地址时自动取消其他地址的 is_default | wait 2s, lease 5s | `JstUserAddressService.setDefault` |
