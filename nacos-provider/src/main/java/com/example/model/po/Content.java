package com.example.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 内容服务配置
 *
 * @TableName t_content
 */
@TableName(value = "t_content")
@Data
public class Content implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 服务名
     */
    @TableField(value = "name")
    private String name;
    /**
     * 服务子标题
     */
    @TableField(value = "sub_title")
    private String subTitle;
    /**
     * 主图
     */
    @TableField(value = "main_img")
    private String mainImg;
    /**
     * 详情图
     */
    @TableField(value = "detail_img")
    private String detailImg;
    /**
     * 创建人
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private String createdBy;
    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;
    /**
     * 更新人
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;
    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;
}
