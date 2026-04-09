-- =====================================================================
-- File: 96-migration-user-address.sql
-- Purpose: create jst_user_address for wx address CRUD
-- Idempotent: yes
-- =====================================================================

CREATE TABLE IF NOT EXISTS `jst_user_address` (
  `address_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_mobile` VARCHAR(64) NOT NULL COMMENT '收货人手机号(密文预留)',
  `province` VARCHAR(30) NOT NULL COMMENT '省',
  `city` VARCHAR(30) NOT NULL COMMENT '市',
  `district` VARCHAR(30) NOT NULL COMMENT '区',
  `address_detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `postal_code` VARCHAR(10) DEFAULT NULL COMMENT '邮编',
  `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址:0否 1是',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标记(0存在 2删除)',
  PRIMARY KEY (`address_id`),
  KEY `idx_user_id` (`user_id`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址';
