CREATE TABLE `t_common_config`
(
    `id`               bigint(20)  NOT NULL AUTO_INCREMENT,
    `config_type`      varchar(20) NOT NULL COMMENT '业务类型',
    `config_type_desc` varchar(100) DEFAULT NULL COMMENT '业务类型描述',
    `config_value`     text         DEFAULT NULL COMMENT '配置值',
    `deleted`          tinyint(2)   DEFAULT '0' COMMENT '是否已删除 0—未删除 1—已删除',
    PRIMARY KEY (`id`),
    KEY `config_type_index` (`config_type`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='通用配置表'


CREATE TABLE `t_key_log` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
 `module` varchar(100) DEFAULT NULL COMMENT '模块',
 `func` varchar(100) DEFAULT NULL COMMENT '功能',
 `repeat_key` varchar(100) DEFAULT NULL COMMENT '不重复添加判断key，不为空则模块-功能-key判重',
 `param` text DEFAULT NULL COMMENT '参数',
 `log_info` text DEFAULT NULL COMMENT '日志信息',
 `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `creator` varchar(50) DEFAULT 'SYS' COMMENT '创建人',
 PRIMARY KEY (`id`),
 INDEX `idx_module_func`(`module`, `func`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
