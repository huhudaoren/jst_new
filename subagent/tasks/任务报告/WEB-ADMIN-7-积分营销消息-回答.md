# 任务报告 WEB-ADMIN-7 — 积分、营销与消息管理

## 状态：✅ 完成

## 交付物清单

### 页面增强（13 文件）
| 模块 | 文件 | 主要增强 |
|---|---|---|
| 积分 | `src/views/jst/points/jst_points_account/index.vue` | 搜索字段收敛（ownerType/ownerId）+ 积分高亮 + 详情抽屉（含流水） |
| 积分 | `src/views/jst/points/jst_points_ledger/index.vue` | 业务类型筛选 + 收支标签（+绿/-红）+ 关联业务跳转 |
| 积分 | `src/views/jst/points/jst_points_rule/index.vue` | 类型筛选 + 状态开关 + 编辑弹窗（required/min/max） |
| 积分 | `src/views/jst/points/jst_level_config/index.vue` | 等级名筛选 + 等级序号排序展示 + 编辑弹窗（阈值/权益描述） |
| 积分 | `src/views/jst/points/jst_growth_ledger/index.vue` | 账户/类型/来源搜索 + 收支方向标签 + 详情抽屉 |
| 积分 | `src/views/jst/points/jst_mall_goods/index.vue` | 商品检索 + 状态兼容映射（on/off/on_sale/off_sale）+ 库存预警 `<10` 红色标签 + 编辑弹窗 |
| 积分 | `src/views/jst/points/jst_mall_exchange_order/index.vue` | 订单号/用户/状态搜索 + 状态标签 + 发货操作 + 详情抽屉 |
| 营销 | `src/views/jst/marketing/jst_coupon_issue_batch/index.vue` | 批次检索 + 发放/领取统计列 + 详情抽屉（目标 JSON） |
| 营销 | `src/views/jst/marketing/jst_user_coupon/index.vue` | 用户/模板/状态检索 + 状态标签 + 关联订单入口 |
| 营销 | `src/views/jst/marketing/jst_user_rights/index.vue` | 用户/模板/状态检索 + 剩余额度展示 + 核销记录联动 |
| 营销 | `src/views/jst/marketing/jst_rights_writeoff_record/index.vue` | 核销单号/权益/状态/核销时间（精确）检索 + 详情抽屉 |
| 消息 | `src/views/jst/message/jst_message_template/index.vue` | 编码/名称/渠道搜索 + 启停开关 + 编辑弹窗（变量占位符说明） |
| 消息 | `src/views/jst/message/jst_message_log/index.vue` | 模板/渠道/状态/用户/时间（精确）搜索 + 状态标签 + 内容详情抽屉 |

### deprecated 占位页（3 文件）
| 文件 | 处理方式 | 跳转目标 |
|---|---|---|
| `src/views/jst/marketing/jst_coupon_template/index.vue` | 废弃提示 + 按钮跳转 | `/jst/coupon/template` |
| `src/views/jst/marketing/jst_rights_template/index.vue` | 废弃提示 + 按钮跳转 | `/jst/rights/template` |
| `src/views/jst/message/jst_notice/index.vue` | 废弃提示 + 按钮跳转 | `/jst/notice/index` |

## 通用实现说明

1. 13 个增强页统一为精品页骨架：`showSearch + right-toolbar + desktop table + mobile card + pagination + el-empty`
2. 统一接入 `isMobile`（`$store.state.app.device === 'mobile'`），移动端卡片化，抽屉/弹窗可全屏
3. 生成器遗留项（批量勾选、导出、无关输入）已按任务卡清理
4. 权限点已从错误的 `system:*` 改为 `jst:*` 对齐后端
5. 积分值统一颜色：正数 `#67C23A`、负数 `#F56C6C`
6. 金额列统一右对齐；金额显示策略采用“整数分→元，非整型保持原值”

## DoD 校验

| 项 | 状态 |
|---|---|
| 13 个页面搜索/响应式/详情能力齐全 | ✅ |
| 积分收支、优惠券状态、商品库存展示规则正确 | ✅ |
| 3 个重复页标记 deprecated 并可跳转 | ✅ |
| `npm run build:prod` 无编译错误 | ✅ |

## 构建验证

- 执行目录：`D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui`
- 命令：`npm run build:prod`
- 结果：`Build complete`（Exit code 0）
- 备注：存在 webpack 体积告警（asset/entrypoint size limit），不影响构建通过

## 关键约束落地情况

1. 强一致筛选：仅使用后端 Mapper 实际支持的过滤字段，不做伪范围过滤
2. 不改后端 Java、不改小程序、不新增 npm 依赖
3. 保留旧路由文件，避免历史菜单或链接直接 404
