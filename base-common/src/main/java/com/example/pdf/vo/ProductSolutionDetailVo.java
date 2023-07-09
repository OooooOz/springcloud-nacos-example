package com.example.pdf.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class ProductSolutionDetailVo implements Serializable {

    /**
     * 产品系列
     */
    private String series;

    /**
     * 产品名称(取意向单商户组合名称)
     */
    private String name;

    /**
     * 面积
     */
    private String area;

    /**
     * 产品价
     */
    private String price;

    /**
     * 金额
     */
    private String amount;

    /**
     * 计价说明
     */
    private String amountDesc;

}
