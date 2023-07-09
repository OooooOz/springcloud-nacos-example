package com.example.pdf.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductSolutionVo implements Serializable {

    /**
     * 产品解决方案明细集合
     */
    private List<ProductSolutionDetailVo> solutionDetailVos;

    /**
     * 小计(意向单金额)
     */
    private BigDecimal total;

}
