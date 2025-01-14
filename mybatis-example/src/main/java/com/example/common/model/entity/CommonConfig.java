package com.example.common.model.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * 通用配置表
 *
 * @TableName t_common_config
 */
@TableName(value = "t_common_config")
@Data
public class CommonConfig implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 业务类型
     */
    private String configType;
    /**
     * 业务类型描述
     */
    private String configTypeDesc;
    /**
     * 配置值
     */
    private String configValue;
    /**
     * 是否已删除 0—未删除 1—已删除
     */
    @TableLogic
    private Integer deleted;
}
