package com.example.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 内容服务配置 (TContent)实体类
 */
@Data
public class ContentDTO implements Serializable {
    private static final long serialVersionUID = -89405520948710836L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 服务名
     */
    private String name;
    /**
     * 服务子标题
     */
    private String subTitle;
    /**
     * 主图
     */
    private String mainImg;
    /**
     * 详情图
     */
    private String detailImg;

    /**
     * 测试超时时间
     */
    private Integer overTime;

}
