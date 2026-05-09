package com.example.exporting;

import com.example.model.BaseResponse;
import org.commons.exporting.domain.model.ExportTaskCreateRequest;
import org.commons.exporting.domain.model.ExportTaskInfo;
import org.commons.exporting.domain.service.Exporting;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * t_common_config 异步导出接入示例。
 */
@RestController
@RequestMapping("/api/exporting/common-config")
public class CommonConfigExportController {

    private final Exporting exporting;

    public CommonConfigExportController(Exporting exporting) {
        this.exporting = exporting;
    }

    /**
     * 创建 t_common_config 异步导出任务。
     */
    @PostMapping("/task")
    public BaseResponse<ExportTaskInfo> createTask(@RequestBody(required = false) CommonConfigExportRequest request) {
        ExportTaskCreateRequest createRequest = new ExportTaskCreateRequest();
        createRequest.setBusinessSystem(CommonConfigAsyncExportHandler.BUSINESS_SYSTEM);
        createRequest.setBusinessType(CommonConfigAsyncExportHandler.BUSINESS_TYPE);
        createRequest.setTaskName(resolveTaskName(request));
        createRequest.setFileName(resolveFileName(request));
        createRequest.setCreator(resolveCreator(request));

        Map<String, Object> extMap = new LinkedHashMap<>();
        if (request != null && StringUtils.hasText(request.getConfigType())) {
            extMap.put("configType", request.getConfigType().trim());
        }
        if (!extMap.isEmpty()) {
            createRequest.setExtMap(extMap);
        }
        return BaseResponse.SUCCESS(exporting.createTask(createRequest));
    }

    /**
     * 查询示例任务详情。
     */
    @GetMapping("/task/{id}")
    public BaseResponse<ExportTaskInfo> getTask(@PathVariable("id") Long id) {
        return BaseResponse.SUCCESS(exporting.getTask(id));
    }

    private String resolveTaskName(CommonConfigExportRequest request) {
        if (request != null && StringUtils.hasText(request.getTaskName())) {
            return request.getTaskName().trim();
        }
        return "t_common_config异步导出";
    }

    private String resolveFileName(CommonConfigExportRequest request) {
        if (request != null && StringUtils.hasText(request.getFileName())) {
            return request.getFileName().trim();
        }
        return "t_common_config导出.xlsx";
    }

    private String resolveCreator(CommonConfigExportRequest request) {
        if (request != null && StringUtils.hasText(request.getCreator())) {
            return request.getCreator().trim();
        }
        return "system";
    }
}

