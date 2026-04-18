-- ============================================================
-- BACKEND-UX-POLISH-TODO-ROUND-2 · B3 · 发票抬头表
-- 2026-04-19
-- ------------------------------------------------------------
-- 支持小程序端「我的发票抬头」CRUD + 默认抬头设置
-- 端点：GET /jst/wx/user/invoice-title/list
--      POST /jst/wx/user/invoice-title
--      DELETE /jst/wx/user/invoice-title/{id}
--      POST /jst/wx/user/invoice-title/{id}/default
-- 前端入口：jst-uniapp/pages/my/settings.vue:248
-- ============================================================

CREATE TABLE IF NOT EXISTS jst_invoice_title (
  title_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '抬头ID',
  user_id      BIGINT       NOT NULL COMMENT '所属用户ID (jst_user.user_id / sys_user.user_id)',
  title_type   VARCHAR(16)  NOT NULL DEFAULT 'personal' COMMENT '抬头类型: personal个人 / company企业',
  title_name   VARCHAR(128) NOT NULL COMMENT '抬头名称',
  tax_no       VARCHAR(64)  DEFAULT NULL COMMENT '税号 (company 必填)',
  is_default   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否默认: 0否 1是',
  create_by    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time  DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag     CHAR(1)      NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0存在 2删除',
  PRIMARY KEY (title_id),
  KEY idx_user (user_id, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='发票抬头';
