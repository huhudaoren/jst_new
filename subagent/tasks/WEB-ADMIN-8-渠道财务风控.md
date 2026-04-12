# WEB-ADMIN-8 — 渠道补充、财务与风控管理

> 优先级：P1 | 预估：L | Agent：Web Admin Agent

---

## 一、目标

增强 15 个代码生成页面，覆盖渠道细分、财务记录、风控体系、用户补充四大模块。

## 二、渠道补充（5 页）

路径前缀：`ruoyi-ui/src/views/jst/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 1 | `channel/jst_rebate_rule/index.vue` | 返点规则 | +规则名/赛事搜索 +比例展示（%） +启用/禁用开关 +编辑弹窗 |
| 2 | `channel/jst_rebate_ledger/index.vue` | 返点台账 | +渠道/订单号/时间搜索 +收支方向标签 +关联订单跳转 |
| 3 | `channel/jst_rebate_settlement/index.vue` | 返点结算 | +渠道/结算周期/状态搜索 +金额汇总行 +详情抽屉 |
| 4 | `channel/jst_event_settlement/index.vue` | 赛事结算 | +赛事/赛事方/状态搜索 +结算金额 +详情抽屉 |
| 5 | `organizer/jst_channel_auth_apply/index.vue` | 渠道认证申请（内部视角） | +申请编号/用户ID搜索 +审核状态标签 +详情抽屉 |

## 三、财务模块（3 页）

路径前缀：`ruoyi-ui/src/views/jst/finance/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 6 | `jst_payment_pay_record/index.vue` | 打款记录 | +打款单号/渠道/时间搜索 +金额格式化 +打款状态标签 +详情抽屉 |
| 7 | `jst_contract_record/index.vue` | 合同记录 | +合同号/赛事方搜索 +合同状态 +有效期展示 +详情抽屉 |
| 8 | `jst_invoice_record/index.vue` | 发票记录 | +发票号/申请人/状态搜索 +发票类型标签 +金额 +详情抽屉 |

## 四、风控模块（5 页）

路径前缀：`ruoyi-ui/src/views/jst/risk/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 9 | `jst_risk_rule/index.vue` | 风控规则 | +规则名/维度搜索 +启用/禁用开关 +编辑弹窗（阈值/动作配置） |
| 10 | `jst_risk_alert/index.vue` | 风控告警 | +告警级别/规则/时间搜索 +级别标签（low→info/medium→warning/high→danger） +详情抽屉 |
| 11 | `jst_risk_blacklist/index.vue` | 黑名单 | +类型（用户/设备/IP）+值搜索 +添加/移除操作 +过期时间 |
| 12 | `jst_risk_case/index.vue` | 风控案例 | +案例号/用户/状态搜索 +处理状态标签 +详情抽屉（含时间线） |
| 13 | `jst_audit_log/index.vue` | 审计日志 | +操作人/操作类型/时间搜索 +操作详情展开 +只读（无编辑/删除） |

## 五、用户补充（2 页）

路径前缀：`ruoyi-ui/src/views/jst/user/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 14 | `jst_channel/index.vue` | 渠道账户（用户维度） | +用户名/手机/渠道名搜索 +认证状态标签 +详情抽屉 |
| 15 | `jst_student_channel_binding/index.vue` | 学生绑定关系 | +学生/渠道/状态搜索 +绑定/解绑时间 +状态标签 |

## 六、清理重复页（1 页）

| 文件 | 被替代者 |
|---|---|
| `user/jst_user/index.vue` | `user/index.vue` |

## 七、增强规范

同 WEB-ADMIN-5 §五，额外注意：

### 金额展示
- 分→元转换，¥ 前缀，右对齐加粗
- 打款/结算金额用品牌色高亮

### 风控特殊处理
- 告警级别颜色：low→蓝 / medium→橙 / high→红 / critical→深红
- 黑名单操作需二次确认（`el-popconfirm`）
- 审计日志**只读**，不允许编辑/删除操作

### 合同/发票
- 有效期字段用日期范围展示
- 状态流转：draft→active→expired / pending→approved→rejected

## 八、参考
- 渠道精品页：`views/jst/channel/admin-rebate/index.vue`、`admin-settlement/index.vue`
- 风控业务：后端 `jst-risk` 模块实体
- 财务业务：后端 `jst-finance` 模块实体

## 九、DoD
- [ ] 15 个增强页面搜索/响应式/详情三项齐全
- [ ] 金额展示统一
- [ ] 风控告警级别颜色正确
- [ ] 审计日志只读
- [ ] 1 个重复页标记 deprecated
- [ ] `npm run build:prod` 无编译错误
- [ ] 报告交付
