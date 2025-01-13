package com.example.validate.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.group.GroupSequenceProvider;

import com.example.validate.provider.ContractGroupSequenceProvider;

import lombok.Data;

@Data
@GroupSequenceProvider(ContractGroupSequenceProvider.class)
public class GroupSequenceProviderSubmitDTO extends BaseGroupSequenceProviderSubmitDTO {

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
    @NotBlank(message = "公司名称不能为空", groups = {GroupValid.Company.class})
    private String companyName;

}
