package com.example.exporting;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.excel.enums.poi.VerticalAlignmentEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * t_common_config Excel 导出行。
 */
@Data
@NoArgsConstructor
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE)
@HeadStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.GENERAL, wrapped = BooleanEnum.FALSE, borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND,
        fillBackgroundColor = 13, fillForegroundColor = 13)
@HeadRowHeight(20)
@ContentRowHeight(20)
@ColumnWidth(20)
@ContentStyle(verticalAlignment = VerticalAlignmentEnum.CENTER, horizontalAlignment = HorizontalAlignmentEnum.GENERAL, borderLeft = BorderStyleEnum.THIN,
        borderRight = BorderStyleEnum.THIN, borderTop = BorderStyleEnum.THIN, borderBottom = BorderStyleEnum.THIN)
@AllArgsConstructor
public class CommonConfigExportRow {
    @ExcelProperty("主键ID")
    private Long id;

    @ExcelProperty("业务类型")
    private String configType;

    @ExcelProperty("业务类型描述")
    private String configTypeDesc;

    @ExcelProperty("配置值")
    private String configValue;
}

