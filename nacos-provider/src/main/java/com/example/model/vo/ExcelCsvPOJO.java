package com.example.model.vo;

import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能描述: CSV报表文件解析成实体类
 */
@Data
@HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.GENERAL, wrapped = BooleanEnum.FALSE, borderLeft = BorderStyleEnum.NONE,
        borderRight = BorderStyleEnum.NONE, borderTop = BorderStyleEnum.NONE, borderBottom = BorderStyleEnum.NONE,
        fillPatternType = FillPatternTypeEnum.NO_FILL)
@HeadFontStyle(fontHeightInPoints = 11, bold = BooleanEnum.FALSE, color = 10)
public class ExcelCsvPOJO {
    /**
     * 活动日期yyyy/mm/dd
     */
    @CsvBindByName(column = "Date")
    private String date;

    /**
     * 广告活动名称
     */
    @CsvBindByName(column = "Campaign Name")
    private String campaignName;

    /**
     * 广告活动id
     */
    @CsvBindByName(column = "Campaign Id")
    private String campaignId;

    /**
     * 广告组名称
     */
    @CsvBindByName(column = "Ad Group Name")
    private String adGroupName;

    /**
     * 广告组id
     */
    @CsvBindByName(column = "Ad Group Id")
    private String adGroupId;

    /**
     * 用户搜索词
     */
    @CsvBindByName(column = "Searched Keyword")
    private String searchedKeyword;

    /**
     * 关键词
     */
    @CsvBindByName(column = "Bidded Keyword")
    private String biddedKeyword;

    /**
     * 匹配方式
     */
    @CsvBindByName(column = "Match Type")
    private String matchType;

    /**
     * 曝光量
     */
    @CsvBindByName(column = "Impressions")
    private String impressions;

    /**
     * 点击量
     */
    @CsvBindByName(column = "Clicks")
    private String clicks;

    /**
     * 广告点击率=点击量/曝光量
     */
    @CsvBindByName(column = "CTR")
    private BigDecimal CTR;

    /**
     * 花费
     */
    @CsvBindByName(column = "Ad Spend")
    private BigDecimal adSpend;

    /**
     * 广告转化率=广告总订单量/点击量
     */
    @CsvBindByName(column = "Conversion Rate")
    private BigDecimal conversionRate;

    /**
     * 广告总销售额
     */
    @CsvBindByName(column = "Total Attributed Sales")
    private BigDecimal totalAttributedSales;

    /**
     * 广告订单量
     */
    @CsvBindByName(column = "Units Sold")
    private String unitsSold;

    /**
     * 投入产出比=广告总销售额/花费
     */
    @CsvBindByName(column = "RoAS")
    private BigDecimal RoAS;

    @CsvBindByName(column = "Attributed Direct View Sales")
    private BigDecimal attributedDirectViewSales;

    @CsvBindByName(column = "Attributed Direct Click Sales")
    private BigDecimal attributedDirectClickSales;

    @CsvBindByName(column = "Attributed Brand View Sales")
    private BigDecimal attributedBrandViewSales;

    @CsvBindByName(column = "Attributed Brand Click Sales")
    private BigDecimal attributedBrandClickSales;

    @CsvBindByName(column = "Attributed Related Click Sales")
    private BigDecimal attributedRelatedClickSales;

}

