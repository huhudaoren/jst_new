# 任务卡 FIX-5-BE - FIX-3 缺失页面后端接口补齐

## 阶段
阶段 E 收尾 / **jst-event + jst-message + jst-user**

## 背景
FIX-3 新建了 8 个前端页面（成绩/证书/消息/设置/隐私/成绩查询/证书验真/公告消息），但后端 7 个接口不存在，页面全部显示空状态。本卡补齐后端使页面可用。

## 前端已封装的 API（参考 `jst-uniapp/api/score.js` + `cert.js` + `message.js`）

前端已按以下 URL 封装，后端必须严格匹配：

```
GET  /jst/wx/score/my              → 我的成绩列表
GET  /jst/wx/score/{id}            → 成绩详情（可选，列表页已含主要字段）
GET  /jst/wx/cert/my               → 我的证书列表
GET  /jst/wx/cert/{id}             → 证书详情（可选）
POST /jst/wx/cert/{id}/share       → 证书分享（可选，返回分享链接）
GET  /jst/wx/message/my            → 我的消息列表
POST /jst/wx/message/{id}/read     → 标记单条已读
POST /jst/wx/message/read-all      → 全部已读
GET  /jst/public/score/query       → 公开成绩查询（无需登录）
GET  /jst/public/cert/verify       → 证书验真（无需登录）
```

## 交付物

### Part A — 我的成绩（jst-event）
**新建**：`jst-event/.../controller/wx/WxScoreController.java`

- `GET /jst/wx/score/my?contestId=&pageNum=&pageSize=`
  - 查询当前登录用户的已发布成绩列表
  - JOIN `jst_score_record` + `jst_contest` + `jst_enroll_record`
  - 条件：`score.user_id = currentUserId AND score.publish_status = 'published'`
  - 返回 VO：`scoreId / contestId / contestName / category / scoreValue / rank / award / remark / publishTime`
  - 支持按赛事 ID 筛选
  - 分页：若依 `startPage()` + `getDataTable()`
  - 权限：`@ss.hasRole('jst_student')`

**注意**：如果 `jst_score_record` 表不存在（PA-5 才创建），用 `jst_enroll_record` 的 `score_value` 字段兜底：
```sql
SELECT e.enroll_id, c.contest_name, c.category, e.score_value, e.award, e.publish_time
FROM jst_enroll_record e
JOIN jst_contest c ON c.contest_id = e.contest_id
WHERE e.user_id = #{userId} AND e.score_value IS NOT NULL AND e.del_flag = '0'
ORDER BY e.publish_time DESC
```

### Part B — 我的证书（jst-event）
**新建**：`jst-event/.../controller/wx/WxCertController.java`

- `GET /jst/wx/cert/my?pageNum=&pageSize=`
  - 查询当前用户已发放的证书
  - 条件：`cert.user_id = currentUserId AND cert.issue_status IN ('granted','issued')`
  - 返回 VO：`certId / contestId / contestName / certName / certNo / award / certImageUrl / issueTime`
  - 若 `jst_cert_record` 不存在，返回空列表（不报错）
  - 权限：`@ss.hasRole('jst_student')`

### Part C — 消息中心（jst-message）
**新建**：`jst-message/.../controller/wx/WxMessageController.java`

- `GET /jst/wx/message/my?type=&readStatus=&pageNum=&pageSize=`
  - 查询当前用户的消息列表
  - type 筛选：`system` / `business` / 空=全部
  - readStatus 筛选：`0`=未读 / `1`=已读 / 空=全部
  - 返回 VO：`messageId / type / title / content / readStatus / bizType / bizId / createTime`
  - 若 `jst_message` 表不存在或无 `user_id` 列，返回空列表
  - 权限：`@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')`

- `POST /jst/wx/message/{id}/read`
  - 标记单条消息已读：`UPDATE jst_message SET read_status=1 WHERE message_id=#{id} AND user_id=#{currentUserId}`
  - 校验归属
  - 若表不存在，返回成功（幂等）

- `POST /jst/wx/message/read-all`
  - 批量标记全部已读：`UPDATE jst_message SET read_status=1 WHERE user_id=#{currentUserId} AND read_status=0`
  - 若表不存在，返回成功

### Part D — 公开成绩查询（无需登录）
**新建**：`jst-event/.../controller/public_/PublicScoreController.java`

- `GET /jst/public/score/query?name=&mobile=&idNo=&contestId=`
  - 至少传 name + (mobile 或 idNo) 才允许查询
  - 查 `jst_enroll_record` 或 `jst_score_record` + `jst_participant` 匹配
  - 返回：`contestName / scoreValue / rank / award / publishTime`（脱敏，不返回完整手机号/证件号）
  - 无需登录，路由加入 SecurityConfig 白名单
  - 防滥用：加 `@RateLimiter(time=60, count=10)` 或简单的 IP 频次限制

### Part E — 证书验真（无需登录）
**新建**：`jst-event/.../controller/public_/PublicCertController.java`

- `GET /jst/public/cert/verify?certNo=`
  - 按证书编号查 `jst_cert_record`
  - 返回：`certNo / contestName / holderName(脱敏) / award / issueTime / valid(boolean)`
  - valid=true 表示证书有效（issue_status='granted' + del_flag='0'）
  - valid=false 或找不到 → 返回 `{ valid: false, message: "未查询到该证书" }`
  - 无需登录，加入白名单
  - 防滥用同上

### Part F — SecurityConfig 白名单
**修改**：`RuoYi-Vue/ruoyi-framework/.../SecurityConfig.java` 或 `application.yml`

追加免登录路径：
- `/jst/public/score/query`
- `/jst/public/cert/verify`

（检查现有白名单配置方式，保持一致）

### Part G — 测试
**修改**：`test/wx-tests.http`

追加：
- `FIX5-1` 登录 MOCK_1001
- `FIX5-2` GET /jst/wx/score/my → 返回列表（可能空）
- `FIX5-3` GET /jst/wx/cert/my → 返回列表（可能空）
- `FIX5-4` GET /jst/wx/message/my → 返回列表（可能空）
- `FIX5-5` POST /jst/wx/message/read-all → 200
- `FIX5-6` GET /jst/public/score/query?name=测试&mobile=13800001001 → 返回结果或空
- `FIX5-7` GET /jst/public/cert/verify?certNo=CERT_TEST_001 → valid true/false

## DoD
- [ ] 7 个必须接口全部实现（score/my + cert/my + message 3 个 + public 2 个）
- [ ] 公开接口加入 SecurityConfig 白名单
- [ ] 公开接口有频次限制
- [ ] 所有接口有 @PreAuthorize 或白名单
- [ ] 表不存在时返回空列表不报错
- [ ] mvn compile 18 模块 SUCCESS
- [ ] 测试段 7 段追加
- [ ] 任务报告 `FIX-5-BE-回答.md`
- [ ] commit: `feat(be): FIX-5 成绩/证书/消息/公开查询 7 接口`

## 不许做
- ❌ 不许改前端
- ❌ 不许新建 DDL 表（用现有表，不存在则兜底空列表）
- ❌ 不许在公开查询接口返回完整手机号/证件号（必须脱敏）

## 依赖：无
## 优先级：P1（FIX-3 的 8 个页面全部依赖）

---
派发时间：2026-04-10
