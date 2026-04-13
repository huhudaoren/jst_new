# WEB-ADMIN-ID2NAME-FE 交付报告

## 1. 任务目标
按任务卡完成 Web Admin 端 `ID -> Name` 展示替换与跳转改造，统一空值降级规则，并补齐目标页 `autoOpen` 行为。

## 2. 交付结论
- 已完成任务卡涉及 17 个业务页面与 4 个目标页（autoOpen）的前端改造。
- 已统一实现展示规则：`Name 有值优先展示`，`Name 缺失回退 ID`，且不抛错。
- 有跳转目标的名称字段已使用 `el-link`。
- 已同步覆盖 PC 表格、移动端卡片、详情抽屉（页面存在对应结构时均已改）。
- 已执行 `npm run build:prod`，构建通过（仅体积 warning）。

## 3. 页面改造清单

### 3.1 渠道模块（4页）
- `jst_rebate_ledger`：`channelId -> channelName(link)`、`orderId -> orderNo(link)`；同步 mobile/drawer；新增 `goChannel/goOrder`。
- `jst_rebate_rule`：`contestId -> contestName(link)`、`channelId -> channelName(plain)`；同步 mobile；新增 `goContest`。
- `jst_event_settlement`：`partnerId -> partnerName(plain)`、`contestId -> contestName(link)`；同步 mobile/drawer；新增 `goContest`。
- `jst_rebate_settlement`：`channelId -> channelName(plain)`；同步 mobile/drawer。

### 3.2 订单模块（2页）
- `jst_appointment_record`：`orderId -> orderNo(link)`；同步 drawer；新增 `goOrder`。
- `jst_order_item`：已核验满足“`orderNo` 优先展示 + 可跳 `admin-order`”，未做功能性结构改造。

### 3.3 积分模块（3页）
- `jst_points_account`：`ownerId -> ownerName(link /jst/user)`、`currentLevelId -> levelName(plain)`；同步 mobile/drawer；新增 `goUser`。
- `jst_points_ledger`：`ownerId -> ownerName(link /jst/user)`；同步 mobile/drawer；新增 `goUser`。
- `jst_mall_exchange_order`：`userId -> userName(link /jst/user)`、`goodsId -> goodsName(link /jst/mall)`；同步 mobile/drawer；新增 `goUser/goMallGoods`。

### 3.4 营销模块（2页）
- `jst_user_coupon`：`userId -> userName(link)`、`couponTemplateId -> couponTemplateName(plain)`、`orderId -> orderNo(link)`；同步 mobile/drawer；`openOrder` 统一改为 `/jst/order/admin-order?orderId=...&autoOpen=1`。
- `jst_user_rights`：`ownerId -> userName(link)`（保留 ownerType 文案）；同步 mobile/drawer；新增 `goUser`。

### 3.5 用户模块（4页）
- `jst_channel`：`userId -> userName(link /jst/user)`；同步 mobile/drawer；新增 `goUser`。
- `jst_student_channel_binding`：`userId -> studentName(link /jst/user)`、`channelId -> channelName(link /jst/channel)`；同步 mobile；新增 `goUser/goChannel`。
- `participant/index`：认领用户优先 `claimUserName/claimedUserName`，缺失回退 ID；支持跳转 `/jst/user`；同步 mobile/drawer 认领用户字段。
- `user/detail`：嵌套表内认领用户、渠道等 ID 列改为名称优先展示，缺失回退原 ID。

### 3.6 赛事模块（2页）
- `jst_enroll_form_template`：`ownerId -> ownerName(所属赛事名, link /jst/contest)`；同步 mobile/drawer；新增 `goContest`。
- `jst_cert_template`：`ownerId -> ownerName(所属赛事名, link /jst/contest)`；同步 mobile/drawer；新增 `goContest`。

## 4. autoOpen 目标页改造（4页）
- `/jst/channel`：识别 `channelId + autoOpen=1` 自动打开详情抽屉。
- `/jst/user`：识别 `userId + autoOpen=1` 自动打开 `UserDetail`。
- `/jst/contest`：识别 `contestId + autoOpen=1` 自动打开 `ContestDetail`。
- `/jst/mall`：识别 `goodsId + autoOpen=1` 自动打开商品编辑抽屉。
- 以上页面均增加 `lastAutoOpenKey` 去重，避免重复触发。

## 5. 统一跳转契约
- 渠道：`{ channelId, autoOpen: '1' }`
- 用户：`{ userId, autoOpen: '1' }`
- 赛事：`{ contestId, autoOpen: '1' }`
- 商城：`{ goodsId, autoOpen: '1' }`
- 订单：`{ orderId, autoOpen: '1' }`

## 6. 构建验收
执行目录：`RuoYi-Vue/ruoyi-ui`

执行命令：
```bash
npm run build:prod
```

结果：
- 构建成功（`Build complete`）。
- 存在 2 条打包体积 warning（asset size / entrypoint size），无编译错误。

## 7. 备注
- 本次仅做前端展示与跳转改造，不涉及后端接口协议调整。
- 保持搜索、分页、权限按钮、详情加载等原有行为不回归。
