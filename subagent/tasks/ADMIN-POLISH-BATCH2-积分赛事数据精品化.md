# ADMIN-POLISH-BATCH2 — 积分商城 + 赛事数据精品化（13 页）

> 优先级：P1 | 预估：L | Agent：Web Admin Agent

---

## 一、改造标准

同 BATCH1 的 S1-S6 标准（Hero区 / 搜索精简 / 表格列优化 / 弹窗优化 / 操作确认 / 空状态）。

## 二、改造页面清单

### 2.1 积分商城组（7 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 1 | `jst/points/jst_points_account/index.vue` | 积分账户 | 查看所有用户的积分余额 | jst_owner_type | ownerType翻译, 余额格式化(千分位), ownerName显示 |
| 2 | `jst/points/jst_points_ledger/index.vue` | 积分流水 | 查看积分收支明细 | jst_change_type, jst_source_type_points | 收入绿色/支出红色, 余额格式化, 来源翻译 |
| 3 | `jst/points/jst_points_rule/index.vue` | 积分规则 | 配置积分获取与消耗规则 | - | rule_type翻译, reward_mode翻译, 数值格式化 |
| 4 | `jst/points/jst_level_config/index.vue` | 等级配置 | 配置用户等级体系 | - | 等级图标/颜色, 成长值阈值显示 |
| 5 | `jst/points/jst_growth_ledger/index.vue` | 成长值流水 | 查看成长值变更记录 | jst_change_type, jst_source_type_points | 同积分流水风格 |
| 6 | `jst/points/jst_mall_goods/index.vue` | 商城商品 | 管理积分商城商品 | - | 商品图片缩略图, 积分价格, 库存/已售 |
| 7 | `jst/points/jst_mall_exchange_order/index.vue` | 兑换订单 | 查看积分兑换订单 | jst_order_status | 状态标签, 地址格式化, 物流信息 |

### 2.2 赛事数据组（6 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 8 | `jst/event/jst_score_record/index.vue` | 成绩记录 | 查看所有参赛成绩 | - | contestName, participantName(非ID), 分数高亮 |
| 9 | `jst/event/jst_cert_record/index.vue` | 证书记录 | 查看所有证书发放情况 | jst_cert_issue_status | 证书编号, 状态标签, 关联名称 |
| 10 | `jst/event/jst_cert_template/index.vue` | 证书模板 | 管理证书设计模板 | jst_audit_status | 底图缩略图, 审核状态, ownerType翻译 |
| 11 | `jst/event/jst_enroll_form_template/index.vue` | 报名表单模板 | 管理动态报名表单 | jst_audit_status | 字段数量显示, 版本号, 状态 |
| 12 | `jst/event/jst_event_partner/index.vue` | 赛事方档案 | 查看赛事方机构信息 | jst_audit_status | 联系人/电话, 审核状态, 隐藏JSON列 |
| 13 | `jst/event/jst_course_learn_record/index.vue` | 学习记录 | 查看课程学习进度 | - | courseName(非ID), 进度百分比, 时长格式化 |

## 三、DoD

- [ ] 13 个页面全部应用 S1-S6 标准
- [ ] 积分流水收支颜色区分（绿/红）
- [ ] 商品列表有缩略图
- [ ] 证书模板有底图缩略图
- [ ] 所有关联字段显示名称
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
