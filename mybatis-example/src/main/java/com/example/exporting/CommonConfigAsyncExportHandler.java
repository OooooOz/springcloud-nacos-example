package com.example.exporting;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.commons.exporting.domain.model.ExportTaskCreateRequest;
import com.commons.exporting.infrastructure.handle.AsyncExportHandler;
import com.example.common.mapper.CommonConfigMapper;
import com.example.common.model.entity.CommonConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * t_common_config 异步导出处理器示例。
 */
@Component
public class CommonConfigAsyncExportHandler implements AsyncExportHandler<CommonConfigExportRow> {

    public static final String BUSINESS_SYSTEM = "mybatis-example";
    public static final String BUSINESS_TYPE = "COMMON_CONFIG_EXPORT";
    private static final String EXT_CONFIG_TYPE = "configType";

    private final CommonConfigMapper commonConfigMapper;

    public CommonConfigAsyncExportHandler(CommonConfigMapper commonConfigMapper) {
        this.commonConfigMapper = commonConfigMapper;
    }

    @Override
    public String businessSystem() {
        return BUSINESS_SYSTEM;
    }

    @Override
    public String businessType() {
        return BUSINESS_TYPE;
    }

    @Override
    public Class<CommonConfigExportRow> headClass() {
        return CommonConfigExportRow.class;
    }

    @Override
    public String sheetName(ExportTaskCreateRequest request) {
        return "通用配置";
    }

    @Override
    public String fileName(ExportTaskCreateRequest request) {
        return "通用配置导出.xlsx";
    }

    @Override
    public void customizeWriter(ExportTaskCreateRequest request, ExcelWriterBuilder writerBuilder) {
        writerBuilder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
    }

    @Override
    public List<CommonConfigExportRow> queryPage(ExportTaskCreateRequest request, long pageNo, int pageSize) {
        Page<CommonConfig> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<CommonConfig> queryWrapper = new LambdaQueryWrapper<>();
        String configType = readString(request.getExtMap(), EXT_CONFIG_TYPE);
        queryWrapper.eq(StringUtils.hasText(configType), CommonConfig::getConfigType, configType)
                .orderByAsc(CommonConfig::getId);
        Page<CommonConfig> result = commonConfigMapper.selectPage(page, queryWrapper);
        if (result == null || result.getRecords() == null || result.getRecords().isEmpty()) {
            return Collections.emptyList();
        }
        return result.getRecords().stream()
                .map(item -> new CommonConfigExportRow(
                        item.getId(),
                        item.getConfigType(),
                        item.getConfigTypeDesc(),
                        item.getConfigValue()))
                .collect(Collectors.toList());
    }

    private String readString(Map<String, Object> extMap, String key) {
        if (extMap == null || extMap.get(key) == null) {
            return null;
        }
        String value = String.valueOf(extMap.get(key)).trim();
        return value.isEmpty() ? null : value;
    }
}

