package com.example.log.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GiantFileParseDTO {

    /**
     * 文件路径
     */
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    /**
     * 分批入库大小
     */
    private Integer batchSave = 2000;

    /**
     * 类型
     */
    private Integer type;
}
