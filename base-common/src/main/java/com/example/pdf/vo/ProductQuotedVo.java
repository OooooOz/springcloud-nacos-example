package com.example.pdf.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductQuotedVo implements Serializable {

    /**
     * 产品方案类型：标配、个性化
     */
    private String type;

    /**
     * 产品报价明细信息集合
     */
    private List<ProductQuotedDetailVo> quotedDetailVos;

    /**
     * 小计(金额汇总)
     */
    private BigDecimal total;

}
