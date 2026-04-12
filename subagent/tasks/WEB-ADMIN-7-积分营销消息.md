# WEB-ADMIN-7 — 积分、营销与消息管理

> 优先级：P1 | 预估：L | Agent：Web Admin Agent

---

## 一、目标

增强 13 个代码生成页面，覆盖积分体系、营销工具、消息中心三大模块。

## 二、积分模块（7 页）

路径前缀：`ruoyi-ui/src/views/jst/points/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 1 | `jst_points_account/index.vue` | 积分账户 | +用户名/手机搜索 +余额/冻结分列高亮 +详情抽屉（含流水） |
| 2 | `jst_points_ledger/index.vue` | 积分流水 | +用户/业务类型/时间范围搜索 +收支方向标签（+绿/-红） +关联业务跳转 |
| 3 | `jst_points_rule/index.vue` | 积分规则 | +规则名/类型搜索 +启用/禁用开关 +编辑弹窗 |
| 4 | `jst_level_config/index.vue` | 等级配置 | +等级名搜索 +等级排序展示 +编辑弹窗（积分阈值/权益描述） |
| 5 | `jst_growth_ledger/index.vue` | 成长值流水 | +用户/时间搜索 +收支方向标签 |
| 6 | `jst_mall_goods/index.vue` | 商城商品 | +商品名/分类搜索 +上下架状态 +库存预警（<10 红色） +编辑弹窗 |
| 7 | `jst_mall_exchange_order/index.vue` | 兑换订单 | +订单号/用户搜索 +兑换状态标签 +发货操作 +详情抽屉 |

## 三、营销模块（4 页）

路径前缀：`ruoyi-ui/src/views/jst/marketing/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 8 | `jst_coupon_issue_batch/index.vue` | 优惠券发放批次 | +批次号/模板名搜索 +发放数量/领取数量统计列 +详情抽屉 |
| 9 | `jst_user_coupon/index.vue` | 用户优惠券 | +用户/券名/状态搜索 +状态标签（unused/used/expired） +关联订单 |
| 10 | `jst_user_rights/index.vue` | 用户权益 | +用户/权益名/状态搜索 +剩余次数展示 +核销记录 |
| 11 | `jst_rights_writeoff_record/index.vue` | 权益核销记录 | +用户/权益/时间搜索 +核销详情 |

## 四、消息模块（2 页）

路径前缀：`ruoyi-ui/src/views/jst/message/`

| # | 文件 | 业务 | 增强要点 |
|---|---|---|---|
| 12 | `jst_message_template/index.vue` | 消息模板 | +模板编码/名称/渠道搜索 +启用/禁用 +编辑弹窗（含变量占位符说明） |
| 13 | `jst_message_log/index.vue` | 消息发送记录 | +用户/消息类型/状态/时间搜索 +发送状态标签 +详情抽屉（含消息内容） |

## 五、清理重复页（3 页）

| 文件 | 被替代者 |
|---|---|
| `marketing/jst_coupon_template/index.vue` | `coupon/template.vue` |
| `marketing/jst_rights_template/index.vue` | `rights/template.vue` |
| `message/jst_notice/index.vue` | `notice/index.vue` |

## 六、增强规范

同 WEB-ADMIN-5 §五，额外注意：

### 积分/金额展示
- 积分值正数绿色 `color: #67C23A`，负数红色 `color: #F56C6C`
- 金额列右对齐
- 库存 <10 用 `el-tag type="danger"` 预警

### 状态标签映射
- 优惠券：unused→info / used→success / expired→info / locked→warning
- 权益：active→success / expired→info / exhausted→danger
- 消息：pending→warning / sent→success / failed→danger
- 商品：on_sale→success / off_sale→info

### 编辑弹窗
- 积分规则、等级配置、商城商品、消息模板需要 `el-dialog` 编辑表单
- 表单必须带 JSR-303 对应的前端校验（required / min / max）

## 七、参考
- 精品页面：`views/jst/coupon/template.vue`（搜索+弹窗编辑模式）
- 积分业务：后端 `jst-points` 模块实体
- 营销业务：后端 `jst-marketing` 模块实体

## 八、DoD
- [ ] 13 个增强页面搜索/响应式/详情三项齐全
- [ ] 积分收支、优惠券状态、商品库存展示正确
- [ ] 3 个重复页标记 deprecated
- [ ] `npm run build:prod` 无编译错误
- [ ] 报告交付
