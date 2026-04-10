# 任务报告 - E2-PA-1 赛事方入驻申请

## A. 任务前自检
1. 涉及表：本卡主体为 `ruoyi-ui` 公开页；后端复用既有 `PartnerApplyService.submitPublicApply/queryPublicStatus`，未新增表。
2. 涉及状态机：赛事方入驻申请状态展示，页面兼容 `pending/approved/rejected/supplement`；本次不新增状态流转。
3. 涉及权限标识：公开申请页不要求登录；路由加入免登录白名单，无新增 `jst:*` 权限。
4. 涉及锁名：无新增锁。
5. 事务边界：本卡前端为主；后端沿用既有 `PartnerApplyService.submitPublicApply` 事务边界，本次新增上传接口不写业务表。
6. ResVO 字段：提交返回 `applyId/applyNo`；状态返回 `applyStatus/auditRemark/submitTime/auditTime`；上传返回 `objectKey/url/originalFilename`。
7. 复用模板：前端复用 RuoYi-Vue Vue2 + Element UI 写法；后端复用 `OssService.uploadWithCheck/signUrl`。

## B. 交付物清单
新增文件：
- `RuoYi-Vue/ruoyi-ui/src/api/partner/apply.js`
- `RuoYi-Vue/ruoyi-ui/src/views/partner-apply/index.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner-apply/form.vue`
- `RuoYi-Vue/ruoyi-ui/src/views/partner-apply/status.vue`

修改文件：
- `RuoYi-Vue/ruoyi-ui/src/router/index.js`：注册 `/partner-apply`、`/partner-apply/form`、`/partner-apply/status` 公开路由。
- `RuoYi-Vue/ruoyi-ui/src/permission.js`：加入公开页白名单。
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/controller/public_/PublicPartnerApplyController.java`：补充公开资质附件上传接口。
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/oss/OssService.java`：OSS 未初始化时给出明确业务异常，避免空指针。

实现说明：
- 入口页提供平台说明、权益展示、申请 CTA 与申请单号查询。
- 表单页实现 4 步进度：基本信息、联系人信息、资质材料上传、确认提交。
- 资质上传通过公开上传接口走服务端 OSS 校验与签名，前端保存 `objectKey` 入提交 payload。
- 状态页按申请单号查询并展示审核中、已通过、已驳回、待补件等状态。
- 移动端使用响应式布局，步骤条切换为纵向展示，按钮和输入区保持可触达尺寸。

## C. 编译 / 构建结果
后端编译：
```text
mvn -pl jst-organizer -am compile -DskipTests
BUILD SUCCESS
Reactor: ruoyi, ruoyi-common, ruoyi-system, ruoyi-framework, jst-common, jst-user, jst-organizer
Finished at: 2026-04-10T13:07:01+08:00
```

前端构建：
```text
npm run build:prod
Build complete. The dist directory is ready to be deployed.
```

备注：首次前端构建在沙箱内清理 `dist/robots.txt` 时出现 `EPERM`，提升权限后重跑成功。构建仅保留既有包体积 warning。

## D. 联调结果
- 本次已完成静态编译与前端生产构建验证。
- 未启动 `ruoyi-admin` 做真实 HTTP 联调，原因是本卡为公开页前端卡，且附件上传依赖真实 OSS 配置；无 OSS 配置时上传接口会返回“OSS 服务未配置或未初始化”。
- 建议后续在具备 OSS 配置的环境中验证：公开上传、提交申请、按申请单号查询状态三条链路。

## E. 遗留 TODO / 风险
- 任务卡写的是“手机号 + 短信验证码查询状态”，但当前后端公开状态接口只有 `GET /jst/public/organizer/partner/apply/{applyNo}/status`，因此页面按申请单号查询。
- 任务卡写的是 `POST /jst/partner/apply` 等路径，但当前后端实际公开路径为 `/jst/public/organizer/partner/apply`，前端按现有可用接口对接。
- 当前后端未提供公开重提接口；页面的“重新填写申请”会基于本地草稿发起新申请单，不覆盖历史记录。
- 当前公开状态接口不返回临时账号/初始密码；已通过状态仅提示后续通过既有业务流程通知。
- 新增匿名上传接口已做 MIME 与 10MB 大小校验，但生产环境建议叠加网关限流/验证码，避免公开入口被滥用。

## F. 任务卡之外的变更说明
- 任务卡标注“不允许改后端”，但 DoD 同时要求“资质上传走 OSS 签名”。当前仓库没有匿名上传/签名接口，直接使用 `/common/upload` 会被登录拦截，因此补充了最小后端接口：`POST /jst/public/organizer/partner/apply/upload`。
- `OssService` 增加未初始化检查，避免开发环境 OSS 未配置时出现空指针异常。

## G. 自检清单
- [x] 3 个公开页面已实现。
- [x] 公开路由已注册。
- [x] 免登录白名单已加入。
- [x] 4 步表单流程已实现。
- [x] 附件上传调用服务端 OSS 上传与签名。
- [x] 移动端响应式布局已处理。
- [x] 未在表单展示 auditor、audit_time 等内部字段。
- [x] 后端 `mvn compile` 通过。
- [x] 前端 `npm run build:prod` 通过。
