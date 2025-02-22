package com.example.log.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName t_key_log
 */
@TableName(value ="t_key_log")
@Data
public class KeyLog implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模块
     */
    private String module;

    /**
     * 功能
     */
    private String func;

    /**
     * 不重复添加判断key，不为空则模块-功能-key判重
     */
    private String repeatKey;

    /**
     * 参数
     */
    private String param;

    /**
     * 日志信息
     */
    private String logInfo;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 创建人
     */
    private String creator;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
