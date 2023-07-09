package com.example.pdf.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class RenovationAttachmentVO implements Serializable {

    /**
     * 附件类型
     */
    private String type;

    /**
     * 门店
     */
    private String store;

    /**
     * 设计师
     */
    private String designer;

    /**
     * 工程地址
     */
    private String address;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 联系电话
     */
    private String customerMobile;

    /**
     * 装修面积
     */
    private String area;

    /**
     * 户型格局
     */
    private String houseType;

    /**
     * 产品解决方案
     */
    private ProductSolutionVo productSolutionVo;

    /**
     * 产品类型报价信息
     */
    private List<ProductQuotedVo> productQuotedVos;

    /**
     * 非工程承包CB的附件数据
     */
    private List<ProductQuotedDetailVo> quotedDetailVos;

    /**
     * 非工程承包CB的附件总计
     */
    private BigDecimal total;

    /**
     * 总金额（意向单报价金额）
     */
    private BigDecimal totalAmount;
}
