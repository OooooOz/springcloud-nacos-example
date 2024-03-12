package com.example.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

/**
 * 用户账户表
 *
 * @TableName t_user_account
 */
@TableName(value = "t_user_account")
@Data
public class UserAccount implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 账户
     */
    private String account;
    /**
     * 客户名称
     */
    private String name;
    /**
     * 客户手机
     */
    private String mobile;
    /**
     * 客户余额
     */
    private BigDecimal amount;
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
