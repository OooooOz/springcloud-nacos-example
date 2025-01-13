package com.example.validate.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupSubmitDTO {

    /**
     * 合同类型：PERSON-个人；COMPANY-对公
     */
    @NotBlank(message = "合同类型不能为空", groups = {GroupValid.Person.class, GroupValid.Company.class})
    private String contractType;

    /**
     * 客户姓名
     */
    @NotBlank(message = "客户姓名不能为空", groups = {GroupValid.Person.class})
    private String customerName;

    /**
     * 客户手机号
     */
    @NotBlank(message = "客户手机号不能为空", groups = {GroupValid.Person.class})
    private String customerMobile;

    /**
     * 营业执照
     */
    @NotBlank(message = "公司营业执照不能为空", groups = {GroupValid.Company.class})
    private String businessLicense;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司公司名称不能为空", groups = {GroupValid.Company.class})
    private String companyName;

    /**
     * 合同附加内容
     */
    @NotBlank(message = "合同附加内容不能为空", groups = {GroupValid.Person.class, GroupValid.Company.class})
    private String content;

}
