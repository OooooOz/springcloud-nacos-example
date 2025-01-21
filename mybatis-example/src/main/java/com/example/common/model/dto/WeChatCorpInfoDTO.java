package com.example.common.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WeChatCorpInfoDTO {

    /**
     * 应用编码（自定义的）,与yml配置文件的appCode值一致，与WeChatWorkAppEnum的枚举value一致
     */
    private String appCode;

    /**
     * 应用id
     */
    private String corpId;
    /**
     * 应用密钥
     */
    private String corpSecret;

    public WeChatCorpInfoDTO(String appCode) {
        this.appCode = appCode;
    }

    public WeChatCorpInfoDTO(String appCode, String corpId, String corpSecret) {
        this.appCode = appCode;
        this.corpId = corpId;
        this.corpSecret = corpSecret;
    }

}
