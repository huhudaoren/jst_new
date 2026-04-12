# F-ANALYSIS-BE — Admin 运营数据聚合 Controller

> 优先级：P2 | 预估：S | Agent：Backend Agent

---

## 一、背景

当前 Admin 数据看板（`ruoyi-ui/src/views/jst/dashboard/index.vue`）通过前端直接调用各模块 list 接口 + pageSize=1 来获取统计数据。这种方式：
- 每次刷新看板发 6 个 HTTP 请求
- 返回完整列表结构只取 total 字段，浪费带宽
- 无法做跨表聚合（如：本月收入 = 订单金额 - 退款金额）

需要一个专用的 Admin Dashboard Controller，提供聚合查询。

## 二、新增 Controller

### `AdminDashboardController.java`

模块：`ruoyi-admin`（放在启动模块，可访问所有 jst-* 模块的 Service）
路径：`com.ruoyi.web.controller.jst.AdminDashboardController`
Base URL：`/jst/admin/dashboard`
权限：`@PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")`

| 方法 | URL | 说明 | 返回 |
|---|---|---|---|
| GET | `/overview` | 总览 KPI（6 指标） | OverviewVO |
| GET | `/trend` | 近 7/30 天趋势 | List<TrendPointVO> |
| GET | `/top-contests` | 报名 Top5 赛事 | List<ContestRankVO> |
| GET | `/top-channels` | 返点 Top5 渠道 | List<ChannelRankVO> |
| GET | `/todo` | 待办事项计数 | TodoVO |

### OverviewVO

```java
public class OverviewVO {
    private Integer todayOrderCount;        // 今日新增订单
    private BigDecimal todayRevenue;        // 今日收入（已支付订单金额合计）
    private BigDecimal monthRevenue;        // 本月收入
    private Integer totalUserCount;         // 累计用户数
    private Integer totalContestCount;      // 累计赛事数
    private Integer totalEnrollCount;       // 累计报名数
}
```

### TrendPointVO

```java
public class TrendPointVO {
    private String date;          // "2026-04-12"
    private Integer orderCount;   // 订单数
    private BigDecimal revenue;   // 收入
    private Integer enrollCount;  // 报名数
}
```

### ContestRankVO

```java
public class ContestRankVO {
    private Long contestId;
    private String contestName;
    private Integer enrollCount;
    private BigDecimal totalRevenue;
}
```

### ChannelRankVO

```java
public class ChannelRankVO {
    private Long channelId;
    private String channelName;
    private Integer studentCount;
    private BigDecimal totalRebate;
}
```

### TodoVO

```java
public class TodoVO {
    private Integer pendingContestAudit;    // 待审赛事
    private Integer pendingEnrollAudit;     // 待审报名
    private Integer pendingRefund;          // 待处理退款
    private Integer pendingWithdraw;        // 待处理提现
    private Integer pendingPartnerApply;    // 待审入驻申请
    private Integer pendingChannelAuth;     // 待审渠道认证
}
```

## 三、Service 层

### `AdminDashboardService.java`

```java
public interface AdminDashboardService {
    OverviewVO getOverview();
    List<TrendPointVO> getTrend(Integer days);  // days=7 or 30
    List<ContestRankVO> getTopContests(Integer limit);
    List<ChannelRankVO> getTopChannels(Integer limit);
    TodoVO getTodo();
}
```

### 实现

**`AdminDashboardServiceImpl.java`**：

- `getOverview()`：直接用 COUNT/SUM SQL 聚合
- `getTrend(days)`：GROUP BY DATE(create_time) 近 N 天
- `getTopContests(limit)`：JOIN jst_enroll_record + jst_order_main GROUP BY contest_id ORDER BY count DESC LIMIT N
- `getTopChannels(limit)`：JOIN jst_rebate_ledger GROUP BY channel_id ORDER BY SUM(amount) DESC LIMIT N
- `getTodo()`：6 个 COUNT 查询

## 四、Mapper

### `AdminDashboardMapper.java` + `AdminDashboardMapper.xml`

放在 `ruoyi-admin` 模块的 mapper 包中（因为需要跨模块访问多张���）。

核心 SQL 示例：

```xml
<!-- 今日订单数+收入 -->
<select id="selectTodayStats" resultType="map">
    SELECT 
        COUNT(*) as orderCount,
        COALESCE(SUM(actual_amount), 0) as revenue
    FROM jst_order_main
    WHERE DATE(create_time) = CURDATE() AND status != 'cancelled' AND del_flag = '0'
</select>

<!-- 近 N 天趋势 -->
<select id="selectTrend" resultType="com.ruoyi.web.controller.jst.vo.TrendPointVO">
    SELECT 
        DATE(create_time) as date,
        COUNT(*) as orderCount,
        COALESCE(SUM(actual_amount), 0) as revenue,
        0 as enrollCount
    FROM jst_order_main
    WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) 
        AND status != 'cancelled' AND del_flag = '0'
    GROUP BY DATE(create_time)
    ORDER BY date
</select>

<!-- 待办计数 -->
<select id="selectTodoCount" resultType="com.ruoyi.web.controller.jst.vo.TodoVO">
    SELECT
        (SELECT COUNT(*) FROM jst_contest WHERE audit_status = 'pending' AND del_flag = '0') as pendingContestAudit,
        (SELECT COUNT(*) FROM jst_enroll_record WHERE audit_status = 'pending' AND del_flag = '0') as pendingEnrollAudit,
        (SELECT COUNT(*) FROM jst_refund_record WHERE status = 'pending' AND del_flag = '0') as pendingRefund,
        (SELECT COUNT(*) FROM jst_withdraw_record WHERE status = 'pending' AND del_flag = '0') as pendingWithdraw,
        (SELECT COUNT(*) FROM jst_event_partner_apply WHERE status = 'pending' AND del_flag = '0') as pendingPartnerApply,
        (SELECT COUNT(*) FROM jst_channel_auth_apply WHERE apply_status = 'pending' AND del_flag = '0') as pendingChannelAuth
</select>
```

## 五、前端适配

**重要**：本卡只做后端。但需要同步通��前端（Admin Dashboard）改用新接口。

当前前端 `api/admin/dashboard.js` 调用 6 个 list 接口 → 改为调用新的 5 个聚合接口。

后端完成后，前端修改很简单：
```javascript
// 旧：6 个 list 请求
// 新：
export function getOverview() { return request({ url: '/jst/admin/dashboard/overview' }) }
export function getTrend(days) { return request({ url: '/jst/admin/dashboard/trend', params: { days } }) }
export function getTopContests() { return request({ url: '/jst/admin/dashboard/top-contests' }) }
export function getTopChannels() { return request({ url: '/jst/admin/dashboard/top-channels' }) }
export function getTodo() { return request({ url: '/jst/admin/dashboard/todo' }) }
```

将此变更记录在报告中，方便前端 Agent 后续改造。

## 六、测试用例

追加到 `test/admin-tests.http`：

```http
### Admin Dashboard - 总览
GET {{baseUrl}}/jst/admin/dashboard/overview
Authorization: Bearer {{adminToken}}

### Admin Dashboard - 趋势(7天)
GET {{baseUrl}}/jst/admin/dashboard/trend?days=7
Authorization: Bearer {{adminToken}}

### Admin Dashboard - Top 赛事
GET {{baseUrl}}/jst/admin/dashboard/top-contests?limit=5
Authorization: Bearer {{adminToken}}

### Admin Dashboard - Top 渠道
GET {{baseUrl}}/jst/admin/dashboard/top-channels?limit=5
Authorization: Bearer {{adminToken}}

### Admin Dashboard - 待办
GET {{baseUrl}}/jst/admin/dashboard/todo
Authorization: Bearer {{adminToken}}
```

## 七、DoD
- [ ] 1 Controller + 5 端点
- [ ] Service + Mapper 实现
- [ ] SQL 聚合查询正确（边界：空表返回 0，不报错）
- [ ] 权限注解 `jst:admin:dashboard`
- [ ] 编译通过
- [ ] .http 测试用例通过
- [ ] 报告中记录前端适配说明
- [ ] 报告交付
