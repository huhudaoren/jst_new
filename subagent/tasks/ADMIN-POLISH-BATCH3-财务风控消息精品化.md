# ADMIN-POLISH-BATCH3 — 财务 + 风控 + 消息 + 字典补齐（10 页 + SQL）

> 优先级：P1 | 预估：M | Agent：Web Admin Agent

---

## 一、改造标准

同 BATCH1 的 S1-S6 标准。

## 二、改造页面清单

### 2.1 财务管理组（3 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 1 | `jst/finance/jst_payment_pay_record/index.vue` | 打款记录 | 查看所有对外打款记录 | jst_audit_status | 金额格式化, 收款方名称(非ID), 状态标签 |
| 2 | `jst/finance/jst_contract_record/index.vue` | 合同管理 | 管理赛事方与渠道合同 | jst_contract_type, jst_audit_status | 合同类型翻译, 附件下载链接, 状态标签 |
| 3 | `jst/finance/jst_invoice_record/index.vue` | 发票管理 | 管理发票申请与开具 | jst_invoice_status | 金额格式化, 发票抬头, 状态标签 |

### 2.2 风控审计组（5 页）

| # | 文件 | Hero 标题 | Hero 描述 | 字典 | 改造要点 |
|---|---|---|---|---|---|
| 4 | `jst/risk/jst_risk_rule/index.vue` | 风控规则 | 配置风控检测规则 | jst_risk_action | 规则类型中文化, 阈值展示(非JSON), 动作标签 |
| 5 | `jst/risk/jst_risk_alert/index.vue` | 风控告警 | 查看风控预警记录 | jst_risk_level | 风险等级彩色标签(低绿/中橙/高红), 目标名称 |
| 6 | `jst/risk/jst_risk_blacklist/index.vue` | 黑白名单 | 管理风控黑白名单 | jst_list_type | 名单类型标签(黑红/白绿), 目标类型翻译 |
| 7 | `jst/risk/jst_risk_case/index.vue` | 风控案例 | 跟踪风控处置案例 | - | 状态标签, 关联告警, 处置时间轴 |
| 8 | `jst/risk/jst_audit_log/index.vue` | 审计日志 | 查看系统操作审计记录 | - | 操作类型中文化, 结果标签(成功绿/失败红), 操作人名称 |

### 2.3 消息管理组（2 页）

| # | 文件 | Hero 标题 | Hero 描述 | 改造要点 |
|---|---|---|---|---|
| 9 | `jst/message/jst_message_template/index.vue` | 消息模板 | 管理系统消息模板 | 通道翻译(站内/短信/微信), 变量高亮显示, 场景翻译 |
| 10 | `jst/message/jst_message_log/index.vue` | 消息发送记录 | 查看消息发送历史 | 发送状态标签(成功/失败/待发), 通道翻译 |

## 三、字典补齐 SQL

产出文件：`架构设计/ddl/99-migration-admin-polish-dict.sql`

在测试库执行以下字典数据（幂等 INSERT）：

```sql
-- 支付方式
INSERT INTO sys_dict_type ... 'jst_pay_method' ...
INSERT INTO sys_dict_data ... wechat→微信支付, bank_transfer→银行转账, points→积分支付, mix→混合支付 ...

-- 商品类型
INSERT INTO sys_dict_type ... 'jst_sku_type' ...
INSERT INTO sys_dict_data ... enroll→报名, appointment_member→预约成员, goods→商品, course→课程 ...

-- 退款类型
INSERT INTO sys_dict_type ... 'jst_refund_type' ...
INSERT INTO sys_dict_data ... refund_only→仅退款, return_refund→退货退款, special_refund→特批退款 ...

-- 变更类型（积分/成长值）
INSERT INTO sys_dict_type ... 'jst_change_type' ...
INSERT INTO sys_dict_data ... earn→获取, spend→消费, freeze→冻结, unfreeze→解冻, adjust→调整, rollback→回退 ...

-- 积分来源
INSERT INTO sys_dict_type ... 'jst_source_type_points' ...
INSERT INTO sys_dict_data ... enroll→报名, course→课程, sign→签到, invite→邀请, learn→学习, award→奖励, exchange→兑换, manual→手动, refund→退款 ...

-- 优惠券类型
INSERT INTO sys_dict_type ... 'jst_coupon_type' ...
INSERT INTO sys_dict_data ... full_reduce→满减券, direct_reduce→直减券, discount→折扣券, contest_specific→赛事专享 ...

-- 权益类型
INSERT INTO sys_dict_type ... 'jst_rights_type' ...
INSERT INTO sys_dict_data ... enroll_deduct→报名抵扣, venue_reduce→场地减免, exclusive_course→专属课程, cs_priority→优先客服, custom→自定义 ...

-- 风险等级
INSERT INTO sys_dict_type ... 'jst_risk_level' ...
INSERT INTO sys_dict_data ... low→低风险, mid→中风险, high→高风险 ...

-- 风控动作
INSERT INTO sys_dict_type ... 'jst_risk_action' ...
INSERT INTO sys_dict_data ... warn→告警, intercept→拦截, manual→人工审核 ...

-- 名单类型
INSERT INTO sys_dict_type ... 'jst_list_type' ...
INSERT INTO sys_dict_data ... black→黑名单, white→白名单 ...

-- 合同类型
INSERT INTO sys_dict_type ... 'jst_contract_type' ...
INSERT INTO sys_dict_data ... partner_coop→赛事方合作, channel_settle→渠道结算, supplement→补充协议 ...

-- 发票状态
INSERT INTO sys_dict_type ... 'jst_invoice_status' ...
INSERT INTO sys_dict_data ... pending→待开票, issued→已开票, void→已作废 ...

-- 核销方式
INSERT INTO sys_dict_type ... 'jst_writeoff_mode' ...
INSERT INTO sys_dict_data ... online_auto→线上自动, manual_review→人工审核, offline_confirm→线下确认 ...

-- 证书状态
INSERT INTO sys_dict_type ... 'jst_cert_issue_status' ...
INSERT INTO sys_dict_data ... draft→草稿, pending→审核中, granted→已颁发, voided→已作废 ...
```

所有 INSERT 使用 `SELECT...FROM DUAL WHERE NOT EXISTS` 幂等模式。

## 四、DoD

- [ ] 10 个页面全部应用 S1-S6 标准
- [ ] 风险等级三色标签（低绿/中橙/高红）
- [ ] 审计日志操作类型中文化
- [ ] 消息模板变量高亮
- [ ] 14 个字典类型 SQL 产出并在测试库执行
- [ ] `npm run build:prod` 通过
- [ ] 报告交付
