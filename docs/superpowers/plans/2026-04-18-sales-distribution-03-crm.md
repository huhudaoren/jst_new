# 销售管理 + 渠道分销 - 计划 #3: CRM 子模块 + 销售工作台前端

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在 plan-01/02 基础上落地 CRM 标准版（跟进记录 / 标签 / 任务）+ 销售工作台前端 5 页面 + 3 个共享 Vue 组件 + 跟进提醒 Quartz + 站内消息推送（WX 模板留接口）。

**Architecture:** Backend 4 个新 Service（Followup/Tag/Task/Notification）+ 3 个 Controller（销售本人 + 主管派任务）；Frontend 复用 ruoyi-ui Element UI + 既有页面布局风格（参考 views/jst/channel）；API 文件按 `api/sales/me/*.js` 组织，路由懒加载注册到 `router/index.js` 销售工作台分组。

**Tech Stack:** 同 plan-02 + Vue 2 + Element UI 2.x（ruoyi-ui 既有依赖）。

**前置:** plan-01 + plan-02 全部合并到当前分支基。

**关联 spec:** §1.5 / §2.5 / §3.6 / §5.5 / §5.6

---

## 文件结构

### Java
**service/**
- `SalesFollowupService.java` + impl
- `SalesChannelTagService.java` + impl
- `SalesFollowupTaskService.java` + impl
- `SalesNotificationService.java` (接口) + `SalesInternalMessageNotificationServiceImpl.java`（站内消息实现） + `SalesWxNotificationServiceImpl.java`（WX 模板消息留接口，本计划仅注释占位）

**controller/sales/**
- `SalesFollowupController.java`
- `SalesChannelTagController.java`
- `SalesFollowupTaskController.java`

**job/**
- `SalesFollowupReminderJob.java`

**Mapper XML 增量**
- `JstSalesFollowupRecordMapper.xml` 加 `selectByChannel` / `selectMineWithFilter` / `countByPeriod` / `selectDueReminders`
- `JstSalesChannelTagMapper.xml` 加 `selectByChannel` / `selectByTagCode`
- `JstSalesFollowupTaskMapper.xml` 加 `selectMine` / `selectAssignedByMe` / `selectOverdue` / `markOverdue`

### SQL
- `架构设计/ddl/99-migration-sales-distribution-followup-quartz.sql` (新增 1 个 Quartz job)

### Frontend (ruoyi-ui)
**api/sales/**
- `followup.js` (跟进 CRUD)
- `tag.js` (渠道标签)
- `task.js` (跟进任务)
- `dashboard.js` (销售工作台首页聚合)
- `channel.js` (我的渠道列表 + 详情 + phone-full)
- `performance.js` (我的业绩)
- `preregister.js` (预录入)

**views/sales/me/**
- `dashboard.vue` (工作台首页 - 含今日待跟进卡片)
- `channels/index.vue` (我的渠道列表)
- `channels/detail.vue` (渠道详情 + 跟进时间线)
- `channels/profile.vue` (渠道画像页)
- `performance.vue` (我的业绩)
- `preregister/index.vue` (我的预录入)
- `tasks/index.vue` (跟进任务列表)

**components/sales/**
- `FollowupTimeline.vue` (跟进时间线组件)
- `FollowupForm.vue` (新建/编辑跟进表单)
- `SalesTagChip.vue` (标签 chip 显示+编辑)
- `FollowupTaskCard.vue` (任务卡片)
- `MaskedAmount.vue` (金额脱敏占位组件)

**Router**
- `router/index.js` 注册销售工作台路由分组

### CLAUDE.md
- 修改顶部 + §六 SALES-DISTRIBUTION 行（标记 plan-03 完成）

---

## Task 1: 准备分支

- [ ] **Step 1: 拉 main 开新分支**

```bash
cd D:/coding/jst_v1
git checkout main
git pull
git checkout -b feature/sales-distribution-03-crm
```

- [ ] **Step 2: 验证 plan-01/02 已合并**

```bash
ls RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/SalesService.java
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "SELECT job_id FROM sys_job WHERE job_id BETWEEN 2001 AND 2006;" jst
```
Expected: 文件存在 + 6 行 Quartz 任务。

---

## Task 2: SalesFollowupService（24h 编辑限制 + 边界 H1-H7）+ 单测

**Files:**
- Create: `service/SalesFollowupService.java` + impl
- Create: 单测 `SalesFollowupServiceImplTest.java`
- Modify: `JstSalesFollowupRecordMapper.xml`

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectByChannel" resultMap="JstSalesFollowupRecordResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_record
         WHERE channel_id = #{channelId}
        ORDER BY followup_at DESC
    </select>

    <select id="selectMineWithFilter" resultMap="JstSalesFollowupRecordResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_record
        <where>
            sales_id = #{salesId}
            <if test="channelId != null">AND channel_id = #{channelId}</if>
            <if test="followupType != null and followupType != ''">AND followup_type = #{followupType}</if>
            <if test="dateFrom != null">AND followup_at &gt;= #{dateFrom}</if>
            <if test="dateTo != null">AND followup_at &lt; #{dateTo}</if>
        </where>
        ORDER BY followup_at DESC
    </select>

    <select id="countByPeriod" resultType="java.util.Map">
        SELECT sales_id, COUNT(*) AS cnt FROM jst_sales_followup_record
         WHERE followup_at &gt;= #{periodStart} AND followup_at &lt; #{periodEnd}
         GROUP BY sales_id
    </select>

    <select id="selectDueReminders" resultMap="JstSalesFollowupRecordResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_record
         WHERE next_followup_at &gt;= #{startOfDay} AND next_followup_at &lt; #{endOfDay}
    </select>
```

加 Mapper 接口方法。

- [ ] **Step 2: SalesFollowupService 接口**

```java
public interface SalesFollowupService {
    JstSalesFollowupRecord create(Long salesId, FollowupCreateReqDTO req);
    /** 24h 内可编辑（H1） */
    void update(Long recordId, Long currentSalesId, FollowupUpdateReqDTO req);
    /** 24h 内可删除（H1） */
    void remove(Long recordId, Long currentSalesId);
    List<JstSalesFollowupRecord> listByChannel(Long channelId);
    List<JstSalesFollowupRecord> listMine(Long salesId, FollowupQueryReqDTO query);
    List<JstSalesFollowupRecord> selectDueRemindersForJob(Date startOfDay, Date endOfDay);
}
```

- [ ] **Step 3: 写测试 8 case**

- create 写入 can_edit_until = now + 24h
- create 时 followupAt = null → 默认取 now
- update 在 24h 内 → 成功，刷新内容但不重置 can_edit_until
- update 超过 24h → ServiceException
- update 别人的跟进 → 鉴权失败
- remove 24h 内 → 成功（物理删除）
- remove 超过 24h → ServiceException
- listByChannel 返按 followup_at DESC

- [ ] **Step 4: 实现 SalesFollowupServiceImpl + 跑测试 + 提交**

```bash
git commit -m "feat(channel): SalesFollowupService 跟进记录 CRUD (24h 编辑限制) + 8 单测"
```

---

## Task 3: SalesChannelTagService（字典 + custom: 自定义标签）+ 单测

**Files:**
- Create: `service/SalesChannelTagService.java` + impl
- Create: 单测
- Modify: `JstSalesChannelTagMapper.xml`

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectByChannel" parameterType="Long" resultMap="JstSalesChannelTagResult">
        SELECT <include refid="cols"/> FROM jst_sales_channel_tag 
        WHERE channel_id = #{channelId} ORDER BY create_time
    </select>

    <select id="selectByTagCode" resultMap="JstSalesChannelTagResult">
        SELECT <include refid="cols"/> FROM jst_sales_channel_tag 
        WHERE tag_code = #{tagCode}
    </select>

    <select id="countByTagCode" resultType="int">
        SELECT COUNT(DISTINCT channel_id) FROM jst_sales_channel_tag 
        WHERE tag_code = #{tagCode}
    </select>
```

- [ ] **Step 2: 接口**

```java
public interface SalesChannelTagService {
    /** 给渠道打标签（字典 code 直接传；自定义需以 'custom:' 前缀） */
    void addTag(Long channelId, String tagCode, String tagColor, Long currentUserId);
    void removeTag(Long id, Long currentUserId);
    List<JstSalesChannelTag> listByChannel(Long channelId);
    /** admin 把 custom: 前缀标签提升为字典（H6） */
    void promoteCustomToDict(String customTagCode, String dictLabel, String listClass);
}
```

- [ ] **Step 3: 实现要点 + 测试 5 case**

- addTag 成功（字典 code）
- addTag 重复（uk 拦截，捕获异常报友好）
- addTag 自定义（custom:VIP客户）→ 写入即可
- removeTag 删除自己/管理员的标签
- promoteCustomToDict 流程：删 custom: 前缀的 tag_code 旧行 + 加新 dict_data + 把 custom 前缀的 tag 行的 tag_code 字段改为新 code

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel): SalesChannelTagService 标签 (字典+custom: 双模 + 提升为字典) + 5 单测"
```

---

## Task 4: SalesFollowupTaskService（任务 + 状态推进 + overdue 自动）

**Files:**
- Create: `service/SalesFollowupTaskService.java` + impl
- Modify: `JstSalesFollowupTaskMapper.xml`
- Create: 单测

- [ ] **Step 1: Mapper XML 追加**

```xml
    <select id="selectMine" resultMap="JstSalesFollowupTaskResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_task
         WHERE assignee_sales_id = #{salesId}
        <if test="status != null and status != ''">AND status = #{status}</if>
        ORDER BY due_date ASC
    </select>

    <select id="selectAssignedByMe" resultMap="JstSalesFollowupTaskResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_task
         WHERE assigner_id = #{userId}
        <if test="status != null and status != ''">AND status = #{status}</if>
        ORDER BY create_time DESC
    </select>

    <select id="selectOverdue" resultType="Long">
        SELECT task_id FROM jst_sales_followup_task
         WHERE status IN ('pending','in_progress') AND due_date &lt; CURDATE()
    </select>

    <update id="markOverdueBatch">
        UPDATE jst_sales_followup_task SET status = 'overdue', update_time = NOW()
         WHERE task_id IN <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
           AND status IN ('pending','in_progress')
    </update>

    <select id="selectDueToday" resultMap="JstSalesFollowupTaskResult">
        SELECT <include refid="cols"/> FROM jst_sales_followup_task
         WHERE status IN ('pending','in_progress') AND due_date = CURDATE()
    </select>
```

- [ ] **Step 2: Service 接口**

```java
public interface SalesFollowupTaskService {
    JstSalesFollowupTask create(TaskCreateReqDTO req, Long assignerUserId);
    /** 销售完成任务，可关联一条 followup_record */
    void complete(Long taskId, Long currentSalesId, Long linkedRecordId);
    void update(Long taskId, TaskUpdateReqDTO req, Long assignerUserId);
    /** 销售看自己的待办 */
    List<JstSalesFollowupTask> listMine(Long salesId, String status);
    /** 主管看自己派出去的 */
    List<JstSalesFollowupTask> listAssignedByMe(Long userId, String status);
    /** Quartz 用：批量标记到期未完成 → overdue */
    int markOverdueByCron();
    List<JstSalesFollowupTask> selectDueTodayForReminder();
}
```

- [ ] **Step 3: 测试 7 case + 实现 + 提交**

```bash
git commit -m "feat(channel): SalesFollowupTaskService 任务 CRUD + 状态机 + 7 单测"
```

---

## Task 5: SalesNotificationService（站内消息实现 + WX 留接口）

**Files:**
- Create: `service/SalesNotificationService.java`
- Create: `service/impl/SalesInternalMessageNotificationServiceImpl.java`
- Create: 占位接口 `service/impl/SalesWxNotificationServiceImpl.java`（@Component 但实际方法只 log，留 WX 接入接口注释）

- [ ] **Step 1: 接口**

```java
package com.ruoyi.jst.channel.service;

public interface SalesNotificationService {
    /** 推送跟进提醒（销售应该跟进哪些渠道） */
    void sendFollowupReminder(Long salesId, java.util.List<FollowupReminderItem> items);
    /** 推送任务提醒 */
    void sendTaskReminder(Long salesId, java.util.List<TaskReminderItem> items);
    /** 高额提成单告警（admin 收） */
    void sendHighAmountAlertToAdmin(Long ledgerId, java.math.BigDecimal amount);
}
```

POJO `FollowupReminderItem` / `TaskReminderItem` 各自含必要字段。

- [ ] **Step 2: 站内消息实现**

```java
@Service
@Primary
public class SalesInternalMessageNotificationServiceImpl implements SalesNotificationService {

    @Autowired
    private com.ruoyi.jst.message.service.JstMessageService messageService;  // 复用既有

    @Override
    public void sendFollowupReminder(Long salesId, List<FollowupReminderItem> items) {
        if (items == null || items.isEmpty()) return;
        // 通过 jst_sales 反查 sys_user_id
        // 构造 JstMessage 写入站内消息
        StringBuilder content = new StringBuilder("您今天有 " + items.size() + " 个待跟进：\n");
        for (FollowupReminderItem it : items) {
            content.append("- ").append(it.channelName)
                   .append(" (上次跟进: ").append(it.lastFollowupAt).append(")\n");
        }
        messageService.sendInternal(salesIdToSysUserId(salesId), 
            "跟进提醒", content.toString(), "sales_followup_reminder");
    }

    @Override
    public void sendTaskReminder(Long salesId, List<TaskReminderItem> items) {
        // 类似实现
    }

    @Override
    public void sendHighAmountAlertToAdmin(Long ledgerId, BigDecimal amount) {
        // 调 messageService.broadcastToRole("admin", ...)
    }
}
```

- [ ] **Step 3: WX 占位实现（不 @Primary，本期不启用）**

```java
@Service
@org.springframework.context.annotation.Profile("wx-template-enabled")  // 默认 disable
public class SalesWxNotificationServiceImpl implements SalesNotificationService {
    // TODO: 接入小程序订阅消息接口
    // 模板消息 ID 由 admin 在 jst_message_template 表里配置
    @Override public void sendFollowupReminder(Long salesId, List<FollowupReminderItem> items) { /* TODO */ }
    @Override public void sendTaskReminder(Long salesId, List<TaskReminderItem> items) { /* TODO */ }
    @Override public void sendHighAmountAlertToAdmin(Long ledgerId, BigDecimal amount) { /* TODO */ }
}
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(channel): SalesNotificationService 接口 + 站内消息实现 + WX 占位"
```

---

## Task 6: SalesFollowupReminderJob

**Files:**
- Create: `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/SalesFollowupReminderJob.java`

- [ ] **Step 1: Job 实现**

```java
@Component("salesFollowupReminderJob")
public class SalesFollowupReminderJob {

    @Autowired private SalesFollowupService followupService;
    @Autowired private SalesFollowupTaskService taskService;
    @Autowired private SalesNotificationService notificationService;

    public void execute() {
        // 1. 标记 overdue 任务
        int n1 = taskService.markOverdueByCron();
        // 2. 找今天 due_date 的任务，按 sales 聚合推送
        List<JstSalesFollowupTask> dueTasks = taskService.selectDueTodayForReminder();
        Map<Long, List<TaskReminderItem>> byTaskSales = groupByAssignee(dueTasks);
        for (var e : byTaskSales.entrySet()) {
            notificationService.sendTaskReminder(e.getKey(), e.getValue());
        }
        // 3. 找今天 next_followup_at 的跟进，按 sales 聚合推送
        Date startOfDay = atStartOfToday();
        Date endOfDay = atEndOfToday();
        List<JstSalesFollowupRecord> dueFollowups = followupService.selectDueRemindersForJob(startOfDay, endOfDay);
        Map<Long, List<FollowupReminderItem>> bySales = groupBySales(dueFollowups);
        for (var e : bySales.entrySet()) {
            notificationService.sendFollowupReminder(e.getKey(), e.getValue());
        }
        log.info("[FollowupReminder] overdue={} taskGroups={} followupGroups={}",
                 n1, byTaskSales.size(), bySales.size());
    }
    // groupByAssignee / groupBySales / atStartOfToday / atEndOfToday helper
}
```

- [ ] **Step 2: 注册到 sys_job**

新建 `架构设计/ddl/99-migration-sales-distribution-followup-quartz.sql`：

```sql
INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark) VALUES
(2007, '销售-跟进提醒', 'SALES', 'salesFollowupReminderJob.execute()', '0 0 8 * * ?', '1', '1', '1', 'admin', NOW(), '每天 08:00 推送今日待跟进/到期任务');
```

- [ ] **Step 3: 应用 SQL + 提交**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 jst < "架构设计/ddl/99-migration-sales-distribution-followup-quartz.sql"
git commit -m "feat(quartz): SalesFollowupReminderJob (8:00 推今日待跟进/到期任务) + sys_job 注册"
```

---

## Task 7: SalesFollowupController + SalesChannelTagController + SalesFollowupTaskController

**Files:**
- Create: 3 个 Controller + 各自 ReqDTO

- [ ] **Step 1: SalesFollowupController**

```java
@RestController
@RequestMapping("/sales/me/followup")
public class SalesFollowupController extends BaseSalesController {

    @Autowired private SalesFollowupService followupService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:list')")
    public TableDataInfo list(FollowupQueryReqDTO query) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(followupService.listMine(salesId, query));
    }

    @GetMapping("/by-channel/{channelId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:list')")
    public AjaxResult byChannel(@PathVariable Long channelId) {
        return AjaxResult.success(followupService.listByChannel(channelId));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:followup:add')")
    public AjaxResult create(@Valid @RequestBody FollowupCreateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(followupService.create(salesId, req));
    }

    @PutMapping("/{recordId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:edit')")
    public AjaxResult update(@PathVariable Long recordId, @Valid @RequestBody FollowupUpdateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        followupService.update(recordId, salesId, req);
        return AjaxResult.success();
    }

    @DeleteMapping("/{recordId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:remove')")
    public AjaxResult remove(@PathVariable Long recordId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        followupService.remove(recordId, salesId);
        return AjaxResult.success();
    }
}
```

- [ ] **Step 2: SalesChannelTagController**

```java
@RestController
@RequestMapping("/sales/me/tags")
public class SalesChannelTagController extends BaseSalesController {

    @Autowired private SalesChannelTagService tagService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('sales:me:tags:list')")
    public AjaxResult listByChannel(@RequestParam Long channelId) {
        return AjaxResult.success(tagService.listByChannel(channelId));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:tags:edit')")
    public AjaxResult addTag(@Valid @RequestBody TagAddReqDTO req) {
        Long uid = SecurityUtils.getUserId();
        tagService.addTag(req.getChannelId(), req.getTagCode(), req.getTagColor(), uid);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('sales:me:tags:edit')")
    public AjaxResult removeTag(@PathVariable Long id) {
        tagService.removeTag(id, SecurityUtils.getUserId());
        return AjaxResult.success();
    }
}
```

- [ ] **Step 3: SalesFollowupTaskController**

```java
@RestController
@RequestMapping("/sales/me/tasks")
public class SalesFollowupTaskController extends BaseSalesController {

    @Autowired private SalesFollowupTaskService taskService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:tasks:list')")
    public TableDataInfo listMine(@RequestParam(required = false) String status) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(taskService.listMine(salesId, status));
    }

    @PostMapping("/{taskId}/complete")
    @PreAuthorize("@ss.hasPermi('sales:me:tasks:complete')")
    public AjaxResult complete(@PathVariable Long taskId, 
                               @RequestParam(required = false) Long linkedRecordId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        taskService.complete(taskId, salesId, linkedRecordId);
        return AjaxResult.success();
    }
}

// 主管派任务的接口在 /sales/manager/tasks 路径
@RestController
@RequestMapping("/sales/manager/tasks")
@PreAuthorize("@ss.hasRole('jst_sales_manager') or @ss.hasRole('admin')")
public class SalesManagerTaskController extends BaseSalesController {

    @Autowired private SalesFollowupTaskService taskService;

    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:manager:tasks:assign')")
    public AjaxResult assign(@Valid @RequestBody TaskCreateReqDTO req) {
        return AjaxResult.success(taskService.create(req, SecurityUtils.getUserId()));
    }

    @GetMapping("/assigned-by-me")
    @PreAuthorize("@ss.hasPermi('sales:manager:tasks:list')")
    public AjaxResult listAssignedByMe(@RequestParam(required = false) String status) {
        return AjaxResult.success(taskService.listAssignedByMe(SecurityUtils.getUserId(), status));
    }
}
```

- [ ] **Step 4: 编译 + 提交**

```bash
mvn -pl RuoYi-Vue/jst-channel,RuoYi-Vue/ruoyi-admin -am clean compile -DskipTests -q
git commit -m "feat(channel): SalesFollowup/Tag/Task 3 个 Controller + 主管派任务端点"
```

---

## Task 8: 前端 API 文件

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/followup.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/tag.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/task.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/dashboard.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/channel.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/performance.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/me/preregister.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/sales/manager/task.js`

- [ ] **Step 1: followup.js**

```javascript
import request from '@/utils/request'

export function listMine(query) {
  return request({ url: '/sales/me/followup/list', method: 'get', params: query })
}
export function listByChannel(channelId) {
  return request({ url: `/sales/me/followup/by-channel/${channelId}`, method: 'get' })
}
export function createFollowup(data) {
  return request({ url: '/sales/me/followup', method: 'post', data })
}
export function updateFollowup(recordId, data) {
  return request({ url: `/sales/me/followup/${recordId}`, method: 'put', data })
}
export function removeFollowup(recordId) {
  return request({ url: `/sales/me/followup/${recordId}`, method: 'delete' })
}
```

- [ ] **Step 2: tag.js**

```javascript
import request from '@/utils/request'
export function listTags(channelId) {
  return request({ url: '/sales/me/tags', method: 'get', params: { channelId } })
}
export function addTag(data) {
  return request({ url: '/sales/me/tags', method: 'post', data })
}
export function removeTag(id) {
  return request({ url: `/sales/me/tags/${id}`, method: 'delete' })
}
```

- [ ] **Step 3: task.js**

```javascript
import request from '@/utils/request'
export function listMyTasks(query) {
  return request({ url: '/sales/me/tasks/list', method: 'get', params: query })
}
export function completeTask(taskId, linkedRecordId) {
  return request({ url: `/sales/me/tasks/${taskId}/complete`, method: 'post', params: { linkedRecordId } })
}
```

- [ ] **Step 4: 主管 task.js**

```javascript
// api/sales/manager/task.js
import request from '@/utils/request'
export function assignTask(data) {
  return request({ url: '/sales/manager/tasks', method: 'post', data })
}
export function listAssignedByMe(status) {
  return request({ url: '/sales/manager/tasks/assigned-by-me', method: 'get', params: { status } })
}
```

- [ ] **Step 5: dashboard.js / channel.js / performance.js / preregister.js**

```javascript
// api/sales/me/dashboard.js
export function getDashboard() {
  return request({ url: '/sales/me/dashboard', method: 'get' })
}

// api/sales/me/channel.js
export function listMyChannels(query) {
  return request({ url: '/sales/me/channels', method: 'get', params: query })
}
export function getChannelDetail(id) {
  return request({ url: `/sales/me/channels/${id}`, method: 'get' })
}
export function getPhoneFull(id) {
  return request({ url: `/sales/me/channels/${id}/phone-full`, method: 'get' })
}

// api/sales/me/performance.js
export function getMyPerformance(month) {
  return request({ url: '/sales/me/performance', method: 'get', params: { month } })
}

// api/sales/me/preregister.js
export function listMyPreReg() {
  return request({ url: '/sales/me/pre-register/list', method: 'get' })
}
export function createPreReg(data) {
  return request({ url: '/sales/me/pre-register', method: 'post', data })
}
export function renewPreReg(preId) {
  return request({ url: `/sales/me/pre-register/${preId}/renew`, method: 'post' })
}
export function removePreReg(preId) {
  return request({ url: `/sales/me/pre-register/${preId}`, method: 'delete' })
}
```

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/api/sales/
git commit -m "feat(ui): 销售工作台 API 8 个文件 (followup/tag/task/dashboard/channel/performance/preregister/manager)"
```

---

## Task 9: 共享组件 - MaskedAmount + SalesTagChip + FollowupTimeline

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/sales/MaskedAmount.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/components/sales/SalesTagChip.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/components/sales/FollowupTimeline.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/components/sales/FollowupForm.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/components/sales/FollowupTaskCard.vue`

- [ ] **Step 1: MaskedAmount.vue**

```vue
<template>
  <span class="masked-amount">
    <template v-if="value !== null && value !== undefined">¥ {{ value }}</template>
    <span v-else class="masked-placeholder">由 HR 发放</span>
  </span>
</template>

<script>
export default {
  name: 'MaskedAmount',
  props: { value: { type: [Number, String], default: null } }
}
</script>

<style scoped>
.masked-amount { font-weight: 500; }
.masked-placeholder { color: #909399; font-style: italic; font-weight: 400; }
</style>
```

- [ ] **Step 2: SalesTagChip.vue**

```vue
<template>
  <div class="tag-chip-container">
    <el-tag
      v-for="t in tags"
      :key="t.id"
      :color="t.tagColor"
      :type="getTagType(t.tagCode)"
      closable
      @close="onRemove(t.id)"
      style="margin-right: 6px"
    >
      {{ getTagLabel(t.tagCode) }}
    </el-tag>
    <el-button v-if="editable" size="mini" type="text" icon="el-icon-plus" @click="showAdd = true">添加</el-button>

    <el-dialog title="添加标签" :visible.sync="showAdd" width="380px" append-to-body>
      <el-form>
        <el-form-item label="标签">
          <el-select v-model="newTag" filterable allow-create placeholder="选择字典或输入自定义">
            <el-option v-for="d in tagDict" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="onAdd">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listTags, addTag, removeTag } from '@/api/sales/me/tag'
export default {
  name: 'SalesTagChip',
  props: {
    channelId: { type: Number, required: true },
    editable: { type: Boolean, default: true }
  },
  data() {
    return { tags: [], showAdd: false, newTag: '', tagDict: [] }
  },
  created() {
    this.loadTags()
    this.loadDict()
  },
  methods: {
    async loadTags() {
      const res = await listTags(this.channelId)
      this.tags = res.data || []
    },
    loadDict() {
      this.getDicts('jst_sales_channel_tag').then(r => { this.tagDict = r.data })
    },
    async onAdd() {
      if (!this.newTag) return
      let code = this.newTag
      if (!this.tagDict.find(d => d.value === code)) {
        code = 'custom:' + code  // 自定义
      }
      await addTag({ channelId: this.channelId, tagCode: code })
      this.newTag = ''
      this.showAdd = false
      this.loadTags()
    },
    async onRemove(id) {
      await removeTag(id)
      this.loadTags()
    },
    getTagLabel(code) {
      if (code.startsWith('custom:')) return code.substring(7)
      const d = this.tagDict.find(x => x.value === code)
      return d ? d.label : code
    },
    getTagType(code) {
      const d = this.tagDict.find(x => x.value === code)
      return d ? d.listClass : ''
    }
  }
}
</script>
```

- [ ] **Step 3: FollowupTimeline.vue**

```vue
<template>
  <div class="followup-timeline">
    <el-button size="small" type="primary" icon="el-icon-plus" @click="onAdd" style="margin-bottom: 16px">
      新建跟进
    </el-button>

    <el-timeline v-if="records.length">
      <el-timeline-item
        v-for="r in records"
        :key="r.recordId"
        :timestamp="formatTime(r.followupAt)"
        :type="moodType(r.mood)"
        placement="top"
      >
        <el-card shadow="hover">
          <div style="display:flex; justify-content:space-between; align-items:center">
            <div>
              <el-tag size="mini" :type="typeColor(r.followupType)">{{ typeLabel(r.followupType) }}</el-tag>
              <el-tag v-if="r.mood" size="mini" :type="moodType(r.mood)" style="margin-left:6px">
                {{ moodLabel(r.mood) }}
              </el-tag>
            </div>
            <div v-if="canEdit(r)">
              <el-button size="mini" type="text" @click="onEdit(r)">编辑</el-button>
              <el-button size="mini" type="text" style="color:#f56c6c" @click="onRemove(r.recordId)">删除</el-button>
            </div>
          </div>
          <div style="margin-top:8px; white-space: pre-wrap">{{ r.content }}</div>
          <div v-if="r.attachmentUrls" class="attachments" style="margin-top:8px">
            <el-image
              v-for="(url, i) in parseAttachments(r.attachmentUrls)"
              :key="i"
              :src="url"
              :preview-src-list="parseAttachments(r.attachmentUrls)"
              style="width:80px;height:80px;margin-right:6px"
            />
          </div>
          <div v-if="r.nextFollowupAt" style="margin-top:8px; font-size:12px; color:#909399">
            <i class="el-icon-bell"></i> 下次跟进：{{ formatTime(r.nextFollowupAt) }}
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <el-empty v-else description="还没有跟进记录" />

    <followup-form
      :visible.sync="formVisible"
      :channel-id="channelId"
      :record="editingRecord"
      @saved="loadRecords"
    />
  </div>
</template>

<script>
import { listByChannel, removeFollowup } from '@/api/sales/me/followup'
import FollowupForm from './FollowupForm.vue'
export default {
  name: 'FollowupTimeline',
  components: { FollowupForm },
  props: { channelId: { type: Number, required: true } },
  data() {
    return {
      records: [], formVisible: false, editingRecord: null,
      typeDict: [], moodDict: []
    }
  },
  created() {
    this.loadRecords()
    this.getDicts('jst_sales_followup_type').then(r => this.typeDict = r.data)
    this.getDicts('jst_sales_mood').then(r => this.moodDict = r.data)
  },
  methods: {
    async loadRecords() {
      const res = await listByChannel(this.channelId)
      this.records = res.data || []
    },
    onAdd() { this.editingRecord = null; this.formVisible = true },
    onEdit(r) { this.editingRecord = r; this.formVisible = true },
    async onRemove(id) {
      await this.$confirm('确认删除该跟进记录？', '提示', { type: 'warning' })
      await removeFollowup(id)
      this.$message.success('已删除')
      this.loadRecords()
    },
    canEdit(r) {
      return r.canEditUntil && new Date(r.canEditUntil) > new Date()
    },
    parseAttachments(s) { try { return JSON.parse(s) } catch (e) { return [] } },
    typeLabel(c) { const d = this.typeDict.find(x => x.value === c); return d ? d.label : c },
    typeColor(c) { const d = this.typeDict.find(x => x.value === c); return d ? d.listClass : '' },
    moodLabel(c) { const d = this.moodDict.find(x => x.value === c); return d ? d.label : c },
    moodType(c) {
      return { high: 'success', mid: 'primary', low: 'warning', none: 'danger' }[c] || ''
    },
    formatTime(t) { return t ? new Date(t).toLocaleString() : '' }
  }
}
</script>
```

- [ ] **Step 4: FollowupForm.vue**

```vue
<template>
  <el-dialog :title="record ? '编辑跟进' : '新建跟进'" :visible.sync="syncVisible" width="600px" append-to-body>
    <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
      <el-form-item label="跟进类型" prop="followupType">
        <el-select v-model="form.followupType" placeholder="选择">
          <el-option v-for="d in typeDict" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="跟进时间" prop="followupAt">
        <el-date-picker v-model="form.followupAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
      </el-form-item>
      <el-form-item label="意向" prop="mood">
        <el-radio-group v-model="form.mood">
          <el-radio v-for="d in moodDict" :key="d.value" :label="d.value">{{ d.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" rows="5" maxlength="1000" show-word-limit />
      </el-form-item>
      <el-form-item label="附件">
        <image-upload v-model="form.attachmentUrls" :limit="5" :file-size="5" />
      </el-form-item>
      <el-form-item label="下次跟进">
        <el-date-picker v-model="form.nextFollowupAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="syncVisible = false">取消</el-button>
      <el-button type="primary" @click="onSave">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { createFollowup, updateFollowup } from '@/api/sales/me/followup'
export default {
  name: 'FollowupForm',
  props: {
    visible: Boolean,
    channelId: { type: Number, required: true },
    record: { type: Object, default: null }
  },
  data() {
    return {
      form: this.emptyForm(),
      typeDict: [], moodDict: [],
      rules: {
        followupType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    syncVisible: {
      get() { return this.visible },
      set(v) { this.$emit('update:visible', v) }
    }
  },
  watch: {
    visible(v) {
      if (v) {
        this.form = this.record ? { ...this.record } : this.emptyForm()
      }
    }
  },
  created() {
    this.getDicts('jst_sales_followup_type').then(r => this.typeDict = r.data)
    this.getDicts('jst_sales_mood').then(r => this.moodDict = r.data)
  },
  methods: {
    emptyForm() {
      return {
        channelId: this.channelId,
        followupType: 'phone',
        followupAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        content: '', mood: null, attachmentUrls: '', nextFollowupAt: null
      }
    },
    async onSave() {
      this.$refs.formRef.validate(async (ok) => {
        if (!ok) return
        if (this.record) {
          await updateFollowup(this.record.recordId, this.form)
        } else {
          await createFollowup({ ...this.form, channelId: this.channelId })
        }
        this.$message.success('已保存')
        this.syncVisible = false
        this.$emit('saved')
      })
    }
  }
}
</script>
```

- [ ] **Step 5: FollowupTaskCard.vue**

```vue
<template>
  <el-card shadow="hover" class="task-card" :class="{ overdue: task.status === 'overdue' }">
    <div class="task-header">
      <h4>{{ task.title }}</h4>
      <el-tag size="mini" :type="statusType(task.status)">{{ statusLabel(task.status) }}</el-tag>
    </div>
    <p v-if="task.description" class="task-desc">{{ task.description }}</p>
    <div class="task-meta">
      <span><i class="el-icon-date"></i> 截止：{{ task.dueDate }}</span>
      <span style="margin-left:12px">渠道 #{{ task.channelId }}</span>
    </div>
    <div v-if="canComplete" style="margin-top:12px; text-align:right">
      <el-button size="small" type="success" @click="$emit('complete', task)">标记完成</el-button>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'FollowupTaskCard',
  props: { task: { type: Object, required: true } },
  computed: {
    canComplete() { return ['pending', 'in_progress'].includes(this.task.status) }
  },
  methods: {
    statusType(s) {
      return { pending: 'info', in_progress: 'primary', completed: 'success', overdue: 'danger' }[s] || ''
    },
    statusLabel(s) {
      return { pending: '待办', in_progress: '进行中', completed: '已完成', overdue: '已超时' }[s] || s
    }
  }
}
</script>

<style scoped>
.task-card { margin-bottom: 12px; }
.task-card.overdue { border-left: 4px solid #f56c6c; }
.task-header { display: flex; justify-content: space-between; align-items: center; }
.task-header h4 { margin: 0; }
.task-desc { color: #606266; margin: 8px 0; }
.task-meta { color: #909399; font-size: 12px; }
</style>
```

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/components/sales/
git commit -m "feat(ui): 5 个销售工作台共享组件 (MaskedAmount/SalesTagChip/FollowupTimeline/FollowupForm/FollowupTaskCard)"
```

---

## Task 10: 销售工作台首页 dashboard.vue

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/sales/me/dashboard.vue`

- [ ] **Step 1: 页面**

```vue
<template>
  <div class="sales-dashboard">
    <h2>销售工作台</h2>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card>
          <div class="stat-block">
            <div class="stat-label">本月成交订单</div>
            <div class="stat-value">{{ stats.monthOrderCount || 0 }} 笔</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-block">
            <div class="stat-label">本月覆盖渠道</div>
            <div class="stat-value">{{ stats.monthChannelCount || 0 }} 个</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-block">
            <div class="stat-label">本月待跟进</div>
            <div class="stat-value">{{ stats.dueFollowupsToday || 0 }} 个</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-block">
            <div class="stat-label">本月预录入命中</div>
            <div class="stat-value">{{ stats.monthMatchedCount || 0 }} 个</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <div slot="header">今日待跟进</div>
          <followup-task-card
            v-for="t in todayTasks"
            :key="t.taskId"
            :task="t"
            @complete="onCompleteTask"
          />
          <el-empty v-if="!todayTasks.length" description="今日无待跟进任务" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">业绩明细（金额由 HR 发放）</div>
          <el-table :data="performanceByType">
            <el-table-column prop="businessType" label="业务类型">
              <template slot-scope="s">{{ businessTypeLabel(s.row.businessType) }}</template>
            </el-table-column>
            <el-table-column prop="orderCount" label="订单数" align="right" />
            <el-table-column label="GMV / 提成" align="right">
              <template>
                <masked-amount :value="null" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getDashboard } from '@/api/sales/me/dashboard'
import { listMyTasks, completeTask } from '@/api/sales/me/task'
import FollowupTaskCard from '@/components/sales/FollowupTaskCard.vue'
import MaskedAmount from '@/components/sales/MaskedAmount.vue'

export default {
  name: 'SalesDashboard',
  components: { FollowupTaskCard, MaskedAmount },
  data() {
    return {
      stats: {},
      todayTasks: [],
      performanceByType: [],
      businessDict: []
    }
  },
  created() {
    this.load()
    this.getDicts('jst_sales_business_type').then(r => this.businessDict = r.data)
  },
  methods: {
    async load() {
      const dash = await getDashboard()
      this.stats = dash.data.stats || {}
      this.performanceByType = dash.data.performanceByType || []
      const tasks = await listMyTasks({ status: 'pending' })
      this.todayTasks = (tasks.rows || []).filter(t => t.dueDate === new Date().toISOString().slice(0, 10))
    },
    async onCompleteTask(t) {
      await completeTask(t.taskId)
      this.$message.success('已完成')
      this.load()
    },
    businessTypeLabel(code) {
      const d = this.businessDict.find(x => x.value === code)
      return d ? d.label : code
    }
  }
}
</script>

<style scoped>
.sales-dashboard { padding: 20px; }
.stat-block { padding: 12px 0; text-align: center; }
.stat-label { color: #909399; font-size: 14px; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; margin-top: 8px; }
</style>
```

- [ ] **Step 2: 后端补 SalesDashboardController**

```java
@RestController
@RequestMapping("/sales/me/dashboard")
public class SalesDashboardController extends BaseSalesController {
    @Autowired private SalesDashboardService dashboardService;
    @GetMapping
    public AjaxResult get() {
        return AjaxResult.success(dashboardService.aggregateForCurrentSales());
    }
}
```

`SalesDashboardService.aggregateForCurrentSales()` 聚合调用 SalesPerformanceService、SalesFollowupTaskService 等，组装成一个 DashboardVO。

- [ ] **Step 3: 提交**

```bash
git commit -m "feat(ui): 销售工作台首页 dashboard.vue + SalesDashboardController"
```

---

## Task 11: 我的渠道列表 + 详情 + 画像

**Files:**
- Create: `views/sales/me/channels/index.vue`
- Create: `views/sales/me/channels/detail.vue`
- Create: `views/sales/me/channels/profile.vue`

- [ ] **Step 1: index.vue（渠道列表）**

```vue
<template>
  <div class="my-channels">
    <el-form :inline="true" :model="query">
      <el-form-item label="名称">
        <el-input v-model="query.name" placeholder="搜索渠道" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="loadList">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading">
      <el-table-column prop="channelName" label="渠道名" />
      <el-table-column label="联系电话" width="160">
        <template slot-scope="s">
          {{ s.row.phone }}
          <el-button v-if="isMasked(s.row.phone)" size="mini" type="text" @click="viewFullPhone(s.row)">
            查看完整
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="标签" width="280">
        <template slot-scope="s">
          <sales-tag-chip :channel-id="s.row.channelId" :editable="false" />
        </template>
      </el-table-column>
      <el-table-column label="最近联系" width="140">
        <template slot-scope="s">
          {{ formatRelative(s.row.lastFollowupAt) || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="s">
          <el-button size="mini" @click="$router.push(`/sales-workbench/channels/${s.row.channelId}`)">详情</el-button>
          <el-button size="mini" type="primary" @click="onAddFollowup(s.row)">新建跟进</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="query.pageNum"
      :limit.sync="query.pageSize"
      @pagination="loadList"
    />

    <followup-form
      v-if="followupChannel"
      :visible.sync="followupVisible"
      :channel-id="followupChannel.channelId"
    />
  </div>
</template>

<script>
import { listMyChannels, getPhoneFull } from '@/api/sales/me/channel'
import SalesTagChip from '@/components/sales/SalesTagChip.vue'
import FollowupForm from '@/components/sales/FollowupForm.vue'

export default {
  components: { SalesTagChip, FollowupForm },
  data() {
    return {
      list: [], total: 0, loading: false,
      query: { name: '', pageNum: 1, pageSize: 20 },
      followupVisible: false, followupChannel: null
    }
  },
  created() { this.loadList() },
  methods: {
    async loadList() {
      this.loading = true
      try {
        const res = await listMyChannels(this.query)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    isMasked(phone) { return phone && phone.includes('****') },
    async viewFullPhone(row) {
      const res = await getPhoneFull(row.channelId)
      this.$alert('完整手机号：' + res.data, '查看完整', { type: 'warning' })
      // 后端已写审计，前端无需额外操作
    },
    onAddFollowup(row) { this.followupChannel = row; this.followupVisible = true },
    formatRelative(t) {
      if (!t) return null
      const days = Math.floor((Date.now() - new Date(t).getTime()) / 86400000)
      if (days === 0) return '今天'
      if (days < 7) return days + ' 天前'
      if (days < 30) return Math.floor(days / 7) + ' 周前'
      return Math.floor(days / 30) + ' 月前'
    }
  }
}
</script>
```

- [ ] **Step 2: detail.vue（渠道详情 + 跟进时间线）**

```vue
<template>
  <div class="channel-detail">
    <el-page-header @back="$router.back()" :content="channel.channelName" />
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14">
        <el-card>
          <div slot="header">基本信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="渠道名">{{ channel.channelName }}</el-descriptions-item>
            <el-descriptions-item label="电话">
              {{ channel.phone }}
              <el-button v-if="isMasked(channel.phone)" size="mini" type="text" @click="viewPhone">查看完整</el-button>
            </el-descriptions-item>
            <el-descriptions-item label="状态">{{ channel.status }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ channel.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card style="margin-top: 20px">
          <div slot="header">跟进时间线</div>
          <followup-timeline :channel-id="channelId" />
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card>
          <div slot="header">标签</div>
          <sales-tag-chip :channel-id="channelId" :editable="true" />
        </el-card>

        <el-card style="margin-top: 20px">
          <div slot="header">渠道画像</div>
          <el-button size="small" @click="$router.push(`/sales-workbench/channels/${channelId}/profile`)">
            查看画像
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getChannelDetail, getPhoneFull } from '@/api/sales/me/channel'
import SalesTagChip from '@/components/sales/SalesTagChip.vue'
import FollowupTimeline from '@/components/sales/FollowupTimeline.vue'

export default {
  components: { SalesTagChip, FollowupTimeline },
  data() {
    return { channel: {}, channelId: null }
  },
  created() {
    this.channelId = parseInt(this.$route.params.channelId)
    this.load()
  },
  methods: {
    async load() {
      const res = await getChannelDetail(this.channelId)
      this.channel = res.data
    },
    isMasked(p) { return p && p.includes('****') },
    async viewPhone() {
      const res = await getPhoneFull(this.channelId)
      this.$alert('完整手机号：' + res.data, '查看完整', { type: 'warning' })
    }
  }
}
</script>
```

- [ ] **Step 3: profile.vue（渠道画像）**

```vue
<template>
  <div class="channel-profile">
    <el-page-header @back="$router.back()" content="渠道画像" />
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8"><el-statistic title="跟进次数" :value="profile.followupCount || 0" /></el-col>
      <el-col :span="8"><el-statistic title="累计成交笔数" :value="profile.orderCount || 0" /></el-col>
      <el-col :span="8"><el-statistic title="覆盖业务类型" :value="profile.businessTypes?.length || 0" /></el-col>
    </el-row>
    <!-- 后端返 profile.activityTrend 为月度跟进次数数组，用 echarts 渲染 -->
    <div ref="chartRef" style="width:100%; height:300px; margin-top:20px"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'

export default {
  data() { return { profile: {}, chart: null } },
  async created() {
    const channelId = this.$route.params.channelId
    const res = await request({ url: `/sales/me/channels/${channelId}/profile`, method: 'get' })
    this.profile = res.data
    this.$nextTick(() => this.renderChart())
  },
  methods: {
    renderChart() {
      this.chart = echarts.init(this.$refs.chartRef)
      this.chart.setOption({
        title: { text: '近 6 月跟进活跃度' },
        xAxis: { type: 'category', data: this.profile.activityTrend?.map(x => x.month) || [] },
        yAxis: { type: 'value' },
        series: [{ type: 'line', smooth: true, data: this.profile.activityTrend?.map(x => x.count) || [] }]
      })
    }
  }
}
</script>
```

> 后端补 `GET /sales/me/channels/{id}/profile` 端点 + 服务（聚合跟进次数、订单笔数、活动趋势），实现细节按上述返回结构。

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(ui): 销售我的渠道 列表+详情+画像 3 页 + 后端 profile 端点"
```

---

## Task 12: 我的预录入页 + 我的业绩页 + 跟进任务页

**Files:**
- Create: `views/sales/me/preregister/index.vue`
- Create: `views/sales/me/performance.vue`
- Create: `views/sales/me/tasks/index.vue`

- [ ] **Step 1: preregister/index.vue**

```vue
<template>
  <div>
    <el-button type="primary" icon="el-icon-plus" @click="showAdd = true">新建预录入</el-button>

    <el-table :data="list" style="margin-top:16px">
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="targetName" label="联系人/单位" />
      <el-table-column prop="status" label="状态">
        <template slot-scope="s">
          <el-tag :type="statusType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expireAt" label="过期时间" />
      <el-table-column prop="renewCount" label="已续期" />
      <el-table-column label="操作" width="200">
        <template slot-scope="s">
          <el-button v-if="s.row.status === 'pending' && s.row.renewCount < 2" size="mini" @click="onRenew(s.row)">续期</el-button>
          <el-button v-if="s.row.status === 'pending'" size="mini" type="danger" @click="onRemove(s.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="新建预录入" :visible.sync="showAdd" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" placeholder="渠道方手机号（必填）" />
        </el-form-item>
        <el-form-item label="联系人/单位" prop="targetName">
          <el-input v-model="form.targetName" maxlength="64" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showAdd = false">取消</el-button>
        <el-button type="primary" @click="onSave">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listMyPreReg, createPreReg, renewPreReg, removePreReg } from '@/api/sales/me/preregister'
export default {
  data() {
    return {
      list: [], showAdd: false,
      form: { phone: '', targetName: '' },
      rules: {
        phone: [
          { required: true, message: '请输入', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错', trigger: 'blur' }
        ]
      }
    }
  },
  created() { this.load() },
  methods: {
    async load() {
      const res = await listMyPreReg()
      this.list = res.data || []
    },
    statusType(s) { return { pending: 'primary', matched: 'success', expired: 'info' }[s] },
    statusLabel(s) { return { pending: '待匹配', matched: '已匹配', expired: '已过期' }[s] },
    onSave() {
      this.$refs.formRef.validate(async ok => {
        if (!ok) return
        await createPreReg(this.form)
        this.$message.success('已新增')
        this.showAdd = false
        this.form = { phone: '', targetName: '' }
        this.load()
      })
    },
    async onRenew(row) {
      await renewPreReg(row.preId)
      this.$message.success('已续期 90 天')
      this.load()
    },
    async onRemove(row) {
      await this.$confirm('确认删除？', '提示', { type: 'warning' })
      await removePreReg(row.preId)
      this.load()
    }
  }
}
</script>
```

- [ ] **Step 2: performance.vue（业绩页 + 金额脱敏占位）**

```vue
<template>
  <div class="performance">
    <el-row :gutter="20">
      <el-col :span="6"><el-card><el-statistic title="本月订单" :value="data.orderCount || 0" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="覆盖渠道" :value="data.channelCount || 0" /></el-card></el-col>
      <el-col :span="12">
        <el-card>
          <div style="display:flex; align-items:center; height:100%">
            <i class="el-icon-info" style="font-size:24px; color:#909399; margin-right:12px"></i>
            <span>提成金额由公司 HR 与工资合并发放，详询主管或财务。</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <div slot="header">业务类型分布</div>
      <el-table :data="byType">
        <el-table-column label="业务类型">
          <template slot-scope="s">{{ businessLabel(s.row.businessType) }}</template>
        </el-table-column>
        <el-table-column prop="orderCount" label="笔数" align="right" />
        <el-table-column label="GMV" align="right">
          <template><masked-amount :value="null" /></template>
        </el-table-column>
        <el-table-column label="提成" align="right">
          <template><masked-amount :value="null" /></template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getMyPerformance } from '@/api/sales/me/performance'
import MaskedAmount from '@/components/sales/MaskedAmount.vue'
export default {
  components: { MaskedAmount },
  data() { return { data: {}, byType: [], businessDict: [] } },
  created() {
    this.load()
    this.getDicts('jst_sales_business_type').then(r => this.businessDict = r.data)
  },
  methods: {
    async load() {
      const res = await getMyPerformance()
      this.data = res.data || {}
      // byType 由后端聚合返回
      this.byType = res.data.byType || []
    },
    businessLabel(c) { const d = this.businessDict.find(x => x.value === c); return d ? d.label : c }
  }
}
</script>
```

- [ ] **Step 3: tasks/index.vue**

```vue
<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="loadTasks">
      <el-tab-pane label="待办" name="pending" />
      <el-tab-pane label="进行中" name="in_progress" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已超时" name="overdue" />
    </el-tabs>

    <followup-task-card
      v-for="t in tasks"
      :key="t.taskId"
      :task="t"
      @complete="onComplete"
    />
    <el-empty v-if="!tasks.length" description="无任务" />
  </div>
</template>

<script>
import { listMyTasks, completeTask } from '@/api/sales/me/task'
import FollowupTaskCard from '@/components/sales/FollowupTaskCard.vue'
export default {
  components: { FollowupTaskCard },
  data() { return { activeTab: 'pending', tasks: [] } },
  created() { this.loadTasks() },
  methods: {
    async loadTasks() {
      const res = await listMyTasks({ status: this.activeTab })
      this.tasks = res.rows || []
    },
    async onComplete(t) {
      await completeTask(t.taskId)
      this.$message.success('已完成')
      this.loadTasks()
    }
  }
}
</script>
```

- [ ] **Step 4: 提交**

```bash
git commit -m "feat(ui): 销售工作台 我的预录入/业绩/跟进任务 3 页"
```

---

## Task 13: 路由注册 + 菜单组件路径校验

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/router/index.js`（销售工作台路由）

> 销售工作台菜单已在 plan-01 注册到 sys_menu，但 component 字段填的是 `'sales/me/channels/index'` 等路径。若依默认通过 `Layout` + `getRouters` 接口动态加载这些 component，因此 router/index.js 不需要硬编码路由——只需保证菜单的 component 字段与新建文件的路径一致。

- [ ] **Step 1: 验证 sys_menu 中销售工作台菜单的 component 字段与文件路径匹配**

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p123456 -e "
SELECT menu_id, menu_name, path, component FROM sys_menu 
WHERE menu_id BETWEEN 9785 AND 978999 AND menu_type='C';
" jst
```

预期 component 路径示例：
- `sales/me/channels/index` → `views/sales/me/channels/index.vue`
- `sales/me/performance/index` → `views/sales/me/performance.vue` ⚠️ 不一致

如果菜单 component 是 `sales/me/performance/index` 但文件是 `views/sales/me/performance.vue`，需要修正其中之一：

**方案 A**：调整 SQL 把 component 改为 `sales/me/performance`
```sql
UPDATE sys_menu SET component='sales/me/performance' WHERE menu_id=978502;
```

**方案 B**：把文件移到 `views/sales/me/performance/index.vue`

推荐方案 A（不挪文件位置）。

- [ ] **Step 2: 同样校验其他菜单项**

按照 plan-01 中的 component 字段，新建文件如下：
- `views/sales/me/channels/index.vue` ✓
- `views/sales/me/performance.vue` （component=sales/me/performance） ⚠️ 修 SQL
- `views/sales/me/preregister/index.vue` ✓
- `views/sales/me/tasks/index.vue` ✓

执行修复 SQL：
```sql
UPDATE sys_menu SET component='sales/me/performance' WHERE menu_id=978502;
```

- [ ] **Step 3: 创建 dashboard 菜单（plan-01 中没有，本计划补）**

```sql
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES (978500, '工作台', 9785, 0, 'index', 'sales/me/dashboard', 1, 0, 'C', '0', '0', 'sales:me:dashboard:view', 'dashboard', 'admin', NOW());
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (501, 978500), (502, 978500);
```

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/sales/
git add 架构设计/ddl/99-migration-sales-distribution-followup-quartz.sql
git commit -m "feat(ui+ddl): 销售工作台路由对齐 + 工作台首页菜单注册"
```

---

## Task 14: 全量 build + 启动验证

- [ ] **Step 1: Backend 编译**

```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn clean compile -DskipTests -q
mvn -pl jst-channel test -Dtest=Sales*Test -q
```
Expected: BUILD SUCCESS, 单测全过。

- [ ] **Step 2: Frontend 构建**

```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
npm run build:prod
```
Expected: BUILD SUCCESS, dist/ 生成。

- [ ] **Step 3: 启动 ruoyi-admin + 用销售账号登录验证**

启动后端：
```bash
cd D:/coding/jst_v1/RuoYi-Vue
mvn -pl ruoyi-admin spring-boot:run -DskipTests
```

启动前端：
```bash
cd D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui
npm run dev
```

手工验证：
1. 用 admin 创建一个销售（POST /admin/sales）+ 给 sys_user 加 jst_sales 角色
2. 用销售账号登录前端
3. 验证只能看到「销售工作台」菜单分组
4. 进入"我的渠道" → 列表显示 phone 脱敏
5. 点详情 → 跟进时间线可新建、可看 24h 编辑
6. 点 "查看完整" → 收到完整电话 + admin 端能看到审计日志（可在 sys_oper_log 表里查）
7. 进入"我的业绩" → 金额位显示"由 HR 发放"
8. 主管账号登录 → 能看到"团队管理"菜单 + 切换查看下属
9. admin 账号登录 → 在"用户渠道"分组看到"销售管理"菜单

---

## Task 15: 更新 CLAUDE.md + merge

- [ ] **Step 1: 修改 CLAUDE.md**

将 SALES-DISTRIBUTION 行替换为：

`**P0 🟡** | SALES-DISTRIBUTION | 🟡 计划 #1+#2+#3 完成（DDL+核心+CRM），plan-04 待启动 | 计划 #3 已交付：4 Service (Followup/Tag/Task/Notification) + 1 Quartz Job (FollowupReminder) + 4 Controller (3 销售本人 + 1 主管派任务) + 5 共享组件 (MaskedAmount/SalesTagChip/FollowupTimeline/FollowupForm/FollowupTaskCard) + 7 销售工作台页面 (dashboard/channels-index/channels-detail/channels-profile/performance/preregister/tasks) + 8 API 文件。npm build:prod 绿。下一步 plan-04：渠道分销+admin 后台+反带客户加固。`

- [ ] **Step 2: Commit + merge**

```bash
git add CLAUDE.md
git commit -m "docs: CLAUDE.md 计划 #3 完成 (CRM+销售工作台前端)"
git checkout main
git merge --no-ff feature/sales-distribution-03-crm
```

---

## 自检清单

- [ ] SalesFollowupService 8 单测（24h 编辑边界 H1-H7）
- [ ] SalesChannelTagService 5 单测（字典+custom+提升）
- [ ] SalesFollowupTaskService 7 单测（状态机+overdue）
- [ ] SalesNotificationService 站内消息实现 + WX 占位
- [ ] SalesFollowupReminderJob 注册到 sys_job (job_id=2007)
- [ ] 4 个 Controller (3 销售本人 + 1 主管派)
- [ ] 8 个前端 API 文件
- [ ] 5 个共享组件
- [ ] 7 个销售工作台页面
- [ ] mvn compile + npm build:prod 双绿
- [ ] 手工验证销售/主管/admin 三种角色登录看到的菜单和数据正确
- [ ] CLAUDE.md 已更新

---

**End of Plan #3**
