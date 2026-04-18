# 任务报告 — BACKEND-UX-POLISH-TODO Round 2B · B1 + B3

> **分支**：`feat/backend-round-2b-b1-b3`
> **范围**：B1（批量支付）+ B3（发票抬头），**B2 B4 跳过**
> **完成日期**：2026-04-19
> **编译结果**：`mvn -pl ruoyi-admin -am compile -DskipTests` 18 模块全绿（57 秒）
> **DDL**：`架构设计/ddl/99-migration-invoice-title.sql` 已本地验证

---

## A. 任务前自检（Step 2）

### B1 批量支付
1. **涉及表**：`jst_order_main`（只读校验，不改状态）
2. **涉及状态机**：SM-1（订单主状态机；本端点**不发生跃迁**，订单保持 `pending_pay`，后续 wx.requestPayment 成功后由既有 `/jst/wx/pay/notify` 回调转 `paid`）
3. **涉及权限**：`@ss.hasRole('jst_channel')`
4. **涉及锁**：无（每单独立 prepay，幂等由微信侧 out_trade_no 保证）
5. **事务边界**：`WxChannelOrderServiceImpl.batchPay` 加 `@Transactional(rollbackFor=Exception.class)`
6. **ResVO 字段**：`BatchPayResVO { batchPayNo, totalAmount, count, items[] }` + `BatchPayItemVO { orderId, orderNo, payAmount, merchantPayParams }`
7. **复用样板**：`OrderServiceImpl.createMixedPay` 中 `wxPayService.unifiedOrder(order)` 的调用路径

### B3 发票抬头
1. **涉及表**：`jst_invoice_title`（新建）
2. **涉及状态机**：无
3. **涉及权限**：`@ss.hasRole('jst_student') or @ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')` + Service 层 `userId` 归属二次校验
4. **涉及锁**：无（用户内 CRUD 串行化，冲突极低，事务隔离足够）
5. **事务边界**：`save / softDelete / setDefault` 三个写方法
6. **ResVO 字段**：`InvoiceTitleVO { titleId, titleType, titleName, taxNo, isDefault, createTime, updateTime }`
7. **复用样板**：`JstContractRecordMapper.xml`（resultMap/insert/update/soft-delete 节奏）

---

## B. 交付物清单

### 新增文件（14）
- **DDL**
  - `架构设计/ddl/99-migration-invoice-title.sql`
- **B1 · jst-order 模块（6 文件）**
  - `jst-order/src/main/java/com/ruoyi/jst/order/dto/BatchPayOrderReqDTO.java`
  - `jst-order/src/main/java/com/ruoyi/jst/order/vo/BatchPayResVO.java`
  - `jst-order/src/main/java/com/ruoyi/jst/order/vo/BatchPayItemVO.java`
  - `jst-order/src/main/java/com/ruoyi/jst/order/service/WxChannelOrderService.java`
  - `jst-order/src/main/java/com/ruoyi/jst/order/service/impl/WxChannelOrderServiceImpl.java`
  - `jst-order/src/main/java/com/ruoyi/jst/order/controller/wx/WxChannelOrderController.java`
- **B3 · jst-finance 模块（7 文件）**
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/domain/JstInvoiceTitle.java`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/vo/InvoiceTitleVO.java`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/dto/InvoiceTitleSaveReqDTO.java`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/mapper/JstInvoiceTitleMapper.java`
  - `jst-finance/src/main/resources/mapper/finance/JstInvoiceTitleMapper.xml`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/service/InvoiceTitleService.java`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/service/impl/InvoiceTitleServiceImpl.java`
  - `jst-finance/src/main/java/com/ruoyi/jst/finance/controller/wx/WxInvoiceTitleController.java`

### 修改文件（2）
- `subagent/tasks/BACKEND-UX-POLISH-TODO-ROUND-2.md`（B1/B3 打 ✅ + 实现路径追加）
- `test/wx-tests.http`（追加 10 条 Round 2B 测试块）

---

## C. mvn compile 结果

```
[INFO] --- 18 模块 SUCCESS ---
[INFO] ruoyi, ruoyi-common, ruoyi-system, ruoyi-framework, ruoyi-quartz, ruoyi-generator,
[INFO] jst-common, jst-user, jst-order, jst-event, jst-channel, jst-points, jst-organizer,
[INFO] jst-marketing, jst-message, jst-risk, jst-finance, ruoyi-admin
[INFO] BUILD SUCCESS  Total time: 57.897 s  (2026-04-19 00:44)
```

---

## D. .http 测试条目（10 条）

> 测试用户：`MOCK_1003`（channel 2001）+ `MOCK_1001`（student）
> 实际跑测试需本地启动 ruoyi-admin 并执行 DDL migration。本次仅验证 DDL 与 mvn 编译。

### B1（3 条）
- `ROUND2B-B1-1` 空 orderIds 应 JSR-303 失败 → expect !200
- `ROUND2B-B1-2` orderIds 51 条应 JSR-303 失败 → expect !200
- `ROUND2B-B1-3` 订单不存在应报错 → expect !200

### B3（7 条）
- `ROUND2B-B3-1` 新增个人抬头 → expect 200 + 回填 titleId
- `ROUND2B-B3-2` 企业抬头缺税号 → expect !200（"企业抬头必须填写税号"）
- `ROUND2B-B3-3` 企业抬头含税号成功 → expect 200
- `ROUND2B-B3-4` 列表至少含刚创建条目 → expect 200 + 数组包含
- `ROUND2B-B3-5` 设默认 → expect 200
- `ROUND2B-B3-6` 再查列表 isDefault=1 唯一 → expect 200 + default 数 ≤ 1
- `ROUND2B-B3-7` 软删 → expect 200

---

## E. 遗留 TODO（给前端 agent 参考）

### B1 前端侧
- `jst-uniapp/pages-sub/channel/orders.vue:244` `goBatchPay()` 当前是 toast + 跳 batch-enroll 兜底。前端可改为：
  ```js
  const res = await uni.$request.post('/jst/wx/channel/orders/batch-pay', { orderIds: this.checkedOrderIds })
  // res.data.items 每条含 merchantPayParams，循环 uni.requestPayment(item.merchantPayParams)
  ```
- 成功后可保留 toast 引导用户去 /my/order 查看

### B3 前端侧
- `jst-uniapp/pages/my/settings.vue:248` 发票抬头可从 localStorage 迁移到后端：
  - 新建 `jst-uniapp/api/user.js` `invoiceTitleApi` 四端点
  - 改 settings.vue 加载时调 `/list`，新增/编辑/切换默认走对应端点

### 未做的项
- **B2（通知偏好）** 仍在 §二 表中待做
- **B4（账号注销）** 仍在 §二 表中待做
- **批量合单支付**：B1 本次采用简化方案（每单独立 prepay），若未来需要真合单（wx combinePay 接口）需：
  1. 建 `jst_batch_pay_order` 主表 + `jst_batch_pay_order_item` 子表
  2. `WxPayService` 扩展 `combinePay(List<JstOrderMain>)`
  3. 支付回调增强：按 batchPayNo 分发各子单 paid
  已在 `WxChannelOrderServiceImpl` 写明 TODO 标记。

### fixture 相关（非本次修改，仅提示）
- Round 2A 测试注释说 "fixture: 1005=channel" 有误，1005 在 fixtures 中是 parent 角色。本次 Round 2B 用正确的 channel 用户 1003（channelId=2001）。

---

## F. 我做了任务卡之外的什么

- 无越权。全部变更严格限定在 B1 + B3 范围内。
- 未新建 BizErrorCode（复用已有 `JST_ORDER_NOT_FOUND / JST_ORDER_ILLEGAL_TRANSIT / JST_ORDER_PAY_METHOD_INVALID / JST_COMMON_AUTH_DENIED / JST_COMMON_PARAM_INVALID / JST_CHANNEL_NOT_AUTHED`）。
- 未改 ddl 的任何旧 SQL；新 DDL 独立文件 `99-migration-invoice-title.sql`，`IF NOT EXISTS` 幂等。
- 未改 `jst-uniapp/` 任何文件（硬约束）。
- 未改 pom.xml（jst-order 已有 jst-common 依赖；jst-finance 已有）。
- 未强引入 cross-module mapper —— B1 在 jst-order 内通过 `JstLoginContext.currentChannelId()` 取 channelId（jst-common 已暴露），避免 jst-order 直接依赖 jst-channel 的 `ChannelLookupMapper`。

---

## G. 自检清单（16-安全文档 §8）

- [x] 所有方法 `@PreAuthorize` — B1 `hasRole('jst_channel')`；B3 三 role OR + Service userId 校验
- [x] ReqDTO 有 JSR-303 — `@NotEmpty @Size` / `@NotBlank @Pattern @Size`
- [x] 出参用 ResVO — `BatchPayResVO / InvoiceTitleVO`，禁返 Entity
- [x] 敏感字段脱敏 — N/A（发票抬头无敏感字段，税号本身即业务明文）
- [x] 详情接口归属校验 — Service 层 `requireOwned(userId, titleId)` 越权抛 99902
- [x] 写操作 `@Log` — batch-pay / invoice-title save/delete/default 全标注
- [x] 资金/状态机方法 `@Transactional` — batchPay + 3 写方法全加
- [x] 高并发方法 `jstLockTemplate.execute` — N/A（本端点无并发热点）
- [x] 没有 `${}` 拼 SQL — Mapper XML 全用 `#{}`
- [x] 没有打印明文敏感字段 — log 只输出 userId / titleId / channelId
- [x] 文件 UTF-8 无 BOM — 全部新建文件用 Write tool（符合 Agent 规范）
- [x] 中文字符串直接写，无 Unicode 转义

---

## H. 不确定决策记录

| 决策点 | 选择 | 理由 |
|---|---|---|
| B1 是否建 `jst_batch_pay_order` 表 | **不建**，走简化方案 | 任务卡说"优先简化"。前端已分单调 requestPayment，合单支付属于真支付上线后再优化 |
| B1 Controller 放 jst-order 还是 jst-channel | **jst-order** | jst-channel 无 jst-order 依赖，跨模块依赖成本高；路径 `/jst/wx/channel/orders/batch-pay` 与 Spring 路由无冲突 |
| B1 批量校验失败策略 | **任一失败整批拒绝** | 防止前端半半拒难处理；错误消息含 orderId 便于定位 |
| B1 batchPayNo 格式 | `"BP" + 12位uuid + epochMillis` | 足够审计标记 + 去重；无需入库 |
| B3 放 jst-finance 还是 jst-user | **jst-finance** | 发票业务语义；已有 `JstInvoiceRecord` / `JstContractRecord` 兄弟 |
| B3 delete 语义 | **软删（del_flag='2'）** | 保留已开票关联历史；任务卡明确要求 |
| B3 company 清空 taxNo | **personal 类型强制置 null / company 允许空** | 校验规则只要求 company 有 taxNo，不反向校验 personal 无；更新到 personal 时强制清 taxNo 保持语义干净 |
| B3 setDefault 并发 | **无锁，事务内先清旧 + 标新** | 单用户场景，读己写己并发极低；事务隔离足够 |
