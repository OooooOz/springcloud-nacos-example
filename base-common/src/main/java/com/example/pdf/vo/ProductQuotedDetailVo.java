package com.example.pdf.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;


@Data
public class ProductQuotedDetailVo implements Serializable {

    /**
     * 序号
     */
    private String No;

    /**
     * 编码(取商品id)
     */
    private String code;

    /**
     * 项目名称(取意向单商品名称)
     */
    private String name;

    /**
     * 预算数量(取意向单商品销售数量)
     */
    private String budgetCount;

    /**
     * 单价(意向单中取值)
     */
    private String price;

    /**
     * 单位(意向单中取值)
     */
    private String unit;

    /**
     * 金额(意向单中取值)
     */
    private String amount;

    /**
     * 品牌(意向单中取值)
     */
    private String branch;

    /**
     * 区域
     */
    private String region = StringUtils.EMPTY;

    /**
     * 材料说明
     */
    private String materialDesc = StringUtils.EMPTY;

    /**
     * 组合内商品（标配）标识，1是；0否
     */
    private Integer groupFlag;

}
