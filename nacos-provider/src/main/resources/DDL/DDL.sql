CREATE TABLE `t_content` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
 `name` varchar(32) NOT NULL COMMENT '服务名',
 `sub_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '服务子标题',
 `main_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '主图',
 `detail_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '详情图',
 `created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
 `created_time` datetime NOT NULL COMMENT '创建时间',
 `updated_by` varchar(32) DEFAULT NULL COMMENT '更新人',
 `updated_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='内容服务配置 ';


CREATE TABLE `t_user_account` (
`id` bigint unsigned NOT NULL AUTO_INCREMENT,
`account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '账户',
`name` varchar(20) DEFAULT NULL COMMENT '客户名称',
`mobile` varchar(20) DEFAULT NULL COMMENT '客户手机',
`amount` decimal(16,6) DEFAULT NULL COMMENT '客户余额',
`created_by` varchar(32) DEFAULT NULL COMMENT '创建人',
`created_time` datetime NOT NULL COMMENT '创建时间',
`updated_by` varchar(32) DEFAULT NULL COMMENT '更新人',
`updated_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户账户表';
