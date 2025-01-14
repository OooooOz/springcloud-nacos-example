package com.example.common.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CommonConfigDTO implements Serializable {
    /**
     * 主键id
     */
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
}
