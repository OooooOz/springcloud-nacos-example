package com.example.casewhen.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class SysUserExportVO implements Serializable {
    /**
     * 主键
     */
    @ExcelIgnore
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @ExcelIgnore
    private String password;

    /**
     * 状态
     */
    @ExcelProperty("状态")
    private Integer status;

    /**
     * 描述
     */
    @ExcelIgnore
    private String desc;

    @ExcelIgnore
    private static final long serialVersionUID = 1L;
}
