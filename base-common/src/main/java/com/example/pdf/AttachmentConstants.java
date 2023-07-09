package com.example.pdf;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Description
 * @Author c-zhongwh01
 * @Date 2023/6/14 13:38
 */
public class AttachmentConstants {
    /**
     * 金额
     */
    public static final String CONSTANT_AMOUNT = "金额（元）";
    /**
     * 面积
     */
    public static final String CONSTANT_AREA = "面积（m";
    /**
     * 附件1：报价单
     */
    public static final String CB_ATTACHMENT_ONE = "附件1：报价单";
    /**
     * 附件：施工预算报价单
     */
    public static final String SG_ATTACHMENT_ONE = "附件：施工预算报价单";
    /**
     * 附件：
     */
    public static final String JC_ATTACHMENT_ONE = "附件：";
    /**
     * 门店
     */
    public static final String BASE_STORE = "门店";
    /**
     * 设计师
     */
    public static final String BASE_DESIGNER = "设计师";
    /**
     * 工程地址
     */
    public static final String BASE_ADDRESS = "工程地址";
    /**
     * 客户姓名
     */
    public static final String BASE_CUSTOMER_NAME = "客户姓名";
    /**
     * 联系电话
     */
    public static final String BASE_CUSTOMER_MOBILE = "联系电话";
    /**
     * 装修面积
     */
    public static final String BASE_AREA = "装修面积（m";
    /**
     * 户型格局
     */
    public static final String BASE_HOUSE_TYPE = "户型格局";
    /**
     * 产品解决方案类型
     */
    public static final String PRODUCT_SOLUTION_TYPE = "一、装修产品解决方案";
    /**
     * 产品解决方案- 标配方案
     */
    public static final String PRODUCT_SOLUTION_TYPE_STANDARD = "标配方案";
    /**
     * 产品解决方案- 个性化选配方案
     */
    public static final String PRODUCT_SOLUTION_TYPE_OPTION = "个性化选配方案";
    /**
     * 产品解决方案表头
     */
    public static final List<String> PRODUCT_SOLUTION_TABLE_HEAD = Lists.newArrayList("产品系列", "产品名称", CONSTANT_AREA, "产品价（元）", CONSTANT_AMOUNT, "计价说明");
    /**
     * 产品报价表头
     */
    public static final List<String> PRODUCT_QUOTED_TABLE_HEAD = Lists.newArrayList("编码", "项目名称", "预算数量", "单位", "单价（元）", CONSTANT_AMOUNT, "品牌", "区域", "材料说明");
    /**
     * 小计
     */
    public static final String PRODUCT_AMOUNT_TOTAL = "小计：";
    /**
     * 小计
     */
    public static final String AMOUNT_TOTAL = "总计：";
    /**
     * 空串
     */
    public static final String EMPTY = "";
    /**
     * 非CB合同附件表头
     */
    public static final List<String> PRODUCT_SINGLE_TABLE_HEAD = Lists.newArrayList("序号", "项目名称", "数量", "单位", CONSTANT_AMOUNT);

    private AttachmentConstants() {
    }
}
