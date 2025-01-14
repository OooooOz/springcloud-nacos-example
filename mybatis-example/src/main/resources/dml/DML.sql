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
