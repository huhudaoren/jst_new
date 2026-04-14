# REFACTOR-TODO-P0 — 后端接口补齐（前端已就绪）

> 优先级：P0 | 预估：M | Agent：Backend Agent
> 依赖：REFACTOR-1-DDL、REFACTOR-6-BIZNO 已完成

---

## 一、背景

傻瓜化重构 11 卡前端已全部交付，但以下后端接口尚未实现，导致前端功能无法完整运行。本卡一次性补齐所有 P0+P1 遗留后端接口。

## 二、测试库连接信息

```
url: jdbc:mysql://59.110.53.165:3306/jst_new?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
username: jst_new
password: J8zZpAa4zG8y6a7e
```

## 三、P0 接口（阻塞前端核心功能）

### 3.1 团队报名提交接口

**前端调用方**：`jst-uniapp/pages-sub/contest/team-enroll.vue`
**API 封装**：`jst-uniapp/api/enroll.js → submitTeamEnroll(data)`

```
POST /jst/wx/enroll/team/submit
```

**请求体**：
```json
{
  "contestId": 8201,
  "leader": {
    "name": "张三",
    "phone": "13800000001",
    "idCard": "110101200801011234",
    "school": "第一小学"
  },
  "members": [
    { "name": "李四", "phone": "13800000002", "school": "第一小学" },
    { "name": "王五", "phone": "13800000003", "school": "第二小学" }
  ],
  "slotId": 1001
}
```

**业务逻辑**：
1. 校验赛事存在 + 状态为 online + 报名时间内
2. 校验 `team_min_size <= members.length + 1(leader) <= team_max_size`
3. 校验手机号不重复
4. 校验队长/队员必填字段（根据赛事的 `team_leader_fields` / `team_member_fields`）
5. 为队长创建报名记录（`jst_enroll_record`），audit_status = pending
6. 为每个队员创建报名记录，关联同一 team_appointment_id
7. 如有 slotId，校验容量并预占位
8. 返回团队 ID + 队长 enrollId

**权限**：wx 端用户登录即可（`@Anonymous` 或 token 校验）

### 3.2 WxContestDetailVO 补充字段

**前端调用方**：多个小程序页面（detail.vue / score-detail.vue / cert-detail.vue）

当前 `WxContestDetailVO` 缺少以下字段，需从关联表查询后填充：

```java
// 已有但可能未填充的子表列表
private List<JstContestSchedule> scheduleList;   // 赛程阶段
private List<JstContestAward> awardList;          // 奖项设置
private List<JstContestFaq> faqList;              // 常见问题
private List<JstScoreItem> scoreItemList;         // 成绩项
private List<JstAppointmentSlot> appointmentSlotList; // 预约时间段

// 新增业务状态字段
private Boolean scorePublished;    // 该赛事是否已发布成绩（至少有 1 条 score_record）
private Boolean hasCert;           // 当前用户是否有该赛事的证书
private String certImageUrl;       // 当前用户的证书图片 URL（如有）
private BigDecimal totalScore;     // 当前用户在该赛事的总分（如有）
```

**改动位置**：
- `WxContestDetailVO.java` — 新增字段 + getter/setter
- `WxContestController.getDetail()` 或 `ContestServiceImpl.getWxDetail()` — 查询填充逻辑
- 填充逻辑：
  ```java
  // scorePublished: 查 jst_score_record WHERE contest_id=? LIMIT 1，有则 true
  // hasCert: 查 jst_cert_record WHERE contest_id=? AND user_id=当前用户 LIMIT 1
  // certImageUrl: 同上，取 cert_image_url 字段
  // totalScore: 查 jst_score_record WHERE contest_id=? AND enroll_id IN (当前用户报名)
  ```

## 四、P1 接口（功能增强）

### 4.1 预约时间段查询接口

**前端调用方**：`jst-uniapp/pages-sub/appointment/apply.vue`（兜底，优先从 contestDetail 获取）

```
GET /jst/wx/appointment/contest/{contestId}/slots
```

**返回**：该赛事的所有启用时间段列表，按 slot_date ASC, start_time ASC 排序

```json
{
  "code": 200,
  "data": [
    { "slotId": 1, "slotDate": "2026-04-20", "startTime": "09:00", "endTime": "10:00", "venue": "A厅", "capacity": 20, "bookedCount": 5 },
    { "slotId": 2, "slotDate": "2026-04-20", "startTime": "10:00", "endTime": "11:00", "venue": "A厅", "capacity": 20, "bookedCount": 20 }
  ]
}
```

**改动**：在 `WxAppointmentController` 新增端点，查 `jst_appointment_slot WHERE contest_id=? AND status=1 AND del_flag='0'`

### 4.2 表单模板 Partner 端创建接口

**前端调用方**：`ruoyi-ui/views/partner/contest-edit.vue` Tab C 快速创建 Drawer

当前前端调用的是 admin 级 API（`POST /jst/event/jst_enroll_form_template`），partner 角色可能无权限。需要补充：

```
POST /jst/partner/form-template
```

**逻辑**：
- 与 admin 创建接口相同，但自动设置 `ownerType='partner'` + `ownerId=当前 partnerId`
- 权限：`@PreAuthorize("hasRole('jst_partner')")`
- PartnerScope 切面自动隔离

### 4.3 证书生成接口（简化版）

**前端调用方**：`ruoyi-ui/views/partner/cert-manage.vue` 批量生成按钮

```
POST /jst/partner/cert/generate
```

**请求体**：
```json
{
  "contestId": 8201,
  "templateId": 1001,
  "enrollIds": [8901, 8902, 8903]
}
```

**业务逻辑**：
1. 校验赛事归属 + 模板存在 + 模板已审核通过
2. 对每个 enrollId：
   a. 查报名记录（需已通过审核）
   b. 查成绩记录（如有）
   c. 调用 `BizNoGenerateService.nextNo("cert_no")` 生成证书编号
   d. 创建 `jst_cert_record`（cert_no, template_id, enroll_id, contest_id, issue_status='pending'）
3. **本期不做服务端渲染 PDF/PNG**（layout_json → 图片的渲染引擎复杂度高，后续独立卡处理）
4. 返回生成的证书数量

### 4.4 小程序码生成接口

**前端调用方**：`jst-uniapp/pages-sub/my/cert-detail.vue` 海报中的小程序码

```
GET /jst/wx/wxacode?path=pages-sub/cert/verify&scene=certNo={certNo}
```

**逻辑**：
- 如果微信小程序配置已就绪（`jst.wxauth.mock=false`）：调用微信 `wxacode.getUnlimited` API 生成小程序码
- 如果 Mock 模式：返回一个占位图片 URL（如项目 Logo）
- 缓存：生成后缓存到 Redis 或 OSS，避免重复生成

**如果微信 API 尚未接入**，可先返回占位图，不阻塞前端流程。

## 五、DoD

- [ ] `POST /jst/wx/enroll/team/submit` 团队报名接口
- [ ] `WxContestDetailVO` 补充 5 个字段 + 填充逻辑
- [ ] `GET /jst/wx/appointment/contest/{id}/slots` 预约时间段接口
- [ ] `POST /jst/partner/form-template` Partner 级表单模板创建
- [ ] `POST /jst/partner/cert/generate` 证书批量生成（简化版）
- [ ] `GET /jst/wx/wxacode` 小程序码（Mock 兜底）
- [ ] `mvn compile -DskipTests` 通过
- [ ] 追加 .http 测试用例
- [ ] 报告交付

## 六、注意事项

- 团队报名接口参照已有的个人报名接口（`EnrollRecordServiceImpl`）结构
- WxContestDetailVO 的 scorePublished/hasCert 字段是**当前登录用户相关**的，注意传入 userId
- 证书生成本期只创建记录+编号，不做 PDF 渲染
- 小程序码如微信 API 未配置，返回占位图即可
- 所有新接口必须有 @PreAuthorize 或 wx token 校验
