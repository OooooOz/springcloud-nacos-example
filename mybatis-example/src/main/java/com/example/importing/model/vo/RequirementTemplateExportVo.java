package com.example.importing.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import com.commons.exporting.infrastructure.handle.ExcelSelected;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 要求信息导出数据模型
 */
@Data
@NoArgsConstructor
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE)
@HeadStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.GENERAL, wrapped = BooleanEnum.FALSE, borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillBackgroundColor = 13, fillForegroundColor = 13)
@HeadRowHeight(20)
@ContentRowHeight(20)
@ColumnWidth(16)
@ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.GENERAL, borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN)
public class RequirementTemplateExportVo implements Serializable {

    private static final long serialVersionUID = 2766881189899695456L;
    /**
     * 项目名称
     */
    @ExcelProperty(value = "项目")
    private String inspectionItems;

    /**
     * 检查内容
     */
    @ExcelProperty(value = "检查内容")
    private String inspectionContent;

    /**
     * 检查标准
     */
    @ExcelProperty(value = "检查标准")
    @ColumnWidth(70)
    private String inspectionStandards;

    /**
     * 是否向业主开放文本
     */
    @ExcelProperty(value = "是否向业主开放")
    @ExcelSelected(source = {"是", "否"})
    private String isOpenHomeownersText;

    /**
     * 检查人员
     */
    @ExcelProperty(value = "检查人员")
    private String inspectionUser;

    /**
     * 配置项
     */
    @ExcelProperty(value = "配置项")
    @ExcelSelected(sourceClass = ConfigSelectVo.class)
    private String config;

}
