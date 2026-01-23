-- 创建数据库
CREATE DATABASE IF NOT EXISTS jieleme DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jieleme;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(32) NOT NULL COMMENT '手机号',
  `nickname` VARCHAR(64) NOT NULL COMMENT '昵称',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1正常 0禁用',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL COMMENT '更新时间',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `t_sms_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` VARCHAR(32) NOT NULL COMMENT '手机号',
  `scene` VARCHAR(32) NOT NULL COMMENT '场景: login/register等',
  `request_id` VARCHAR(64) NOT NULL COMMENT '请求ID',
  `code` VARCHAR(16) NOT NULL COMMENT '验证码',
  `expire_at` DATETIME NOT NULL COMMENT '过期时间',
  `used_at` DATETIME DEFAULT NULL COMMENT '使用时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '请求IP',
  PRIMARY KEY (`id`),
  KEY `idx_phone_scene` (`phone`, `scene`),
  KEY `idx_request_id` (`request_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';
