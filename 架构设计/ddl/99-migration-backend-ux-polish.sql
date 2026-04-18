-- =========================================================
-- 2026-04-18 后端 UX 精品化遗留字段补齐 migration
-- 对应任务卡: subagent/tasks/BACKEND-UX-POLISH-TODO.md
-- 分支: feat/backend-ux-polish-todo
-- =========================================================
-- 执行策略：
-- 1. 所有 ALTER 幂等（ADD COLUMN IF NOT EXISTS 在 MySQL 5.7 不支持，需手动判断）
-- 2. 字段全部 nullable / default，向后兼容
-- 3. 业务 Service 读取新字段时用 try/catch 容错
-- =========================================================

-- §2.2 ContractDetailVO 甲乙方字段 (jst_contract_record 新增 4 列)
-- 需要的字段: partyA / partyAName / partyB / partyBName
-- 业务语义: 甲方=平台 (jst), 乙方=合作方 (partner/channel)
-- partyA / partyB 存的是对象ID，partyAName / partyBName 存的是名称快照
ALTER TABLE jst_contract_record
    ADD COLUMN party_a       VARCHAR(32)  DEFAULT NULL COMMENT '甲方(平台)标识，默认jst'                 AFTER ref_settlement_id,
    ADD COLUMN party_a_name  VARCHAR(128) DEFAULT NULL COMMENT '甲方名称快照'                           AFTER party_a,
    ADD COLUMN party_b       VARCHAR(32)  DEFAULT NULL COMMENT '乙方标识(格式 partner:ID / channel:ID)' AFTER party_a_name,
    ADD COLUMN party_b_name  VARCHAR(128) DEFAULT NULL COMMENT '乙方名称快照'                           AFTER party_b;

-- §2.3 InvoiceListVO 物流字段 (jst_invoice_record 新增 3 列)
ALTER TABLE jst_invoice_record
    ADD COLUMN tracking_company VARCHAR(64)  DEFAULT NULL COMMENT '快递公司' AFTER express_status,
    ADD COLUMN tracking_no      VARCHAR(64)  DEFAULT NULL COMMENT '快递单号' AFTER tracking_company,
    ADD COLUMN delivery_email   VARCHAR(128) DEFAULT NULL COMMENT '投递邮箱' AFTER tracking_no;

-- 完成后备注：
-- * ContractRecordVO Service 容错：读新字段 null-safe
-- * InvoiceRecordVO Service 容错：读新字段 null-safe
-- * 旧数据无需回填
