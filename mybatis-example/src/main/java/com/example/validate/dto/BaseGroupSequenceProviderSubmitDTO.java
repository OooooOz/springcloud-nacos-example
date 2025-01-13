package com.example.validate.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BaseGroupSequenceProviderSubmitDTO {

    /**
     * 合同类型：PERSON-个人；COMPANY-对公
     */
    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    /**
     * 合同附加内容
     */
    @NotBlank(message = "合同附加内容不能为空")
    private String content;

}
