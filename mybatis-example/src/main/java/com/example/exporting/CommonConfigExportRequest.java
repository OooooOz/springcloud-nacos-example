package com.example.exporting;

import lombok.Data;

/**
 * t_common_config 异步导出请求。
 */
@Data
public class CommonConfigExportRequest {

    /**
     * 可选：按业务类型过滤。
     */
    private String configType;

    /**
     * 可选：任务名称。
     */
    private String taskName;

    /**
     * 可选：导出文件名。
     */
    private String fileName;

    /**
     * 可选：操作人。
     */
    private String creator;
}

