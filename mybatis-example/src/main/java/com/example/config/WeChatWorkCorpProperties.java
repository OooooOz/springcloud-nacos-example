package com.example.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.example.common.model.dto.WeChatCorpInfoDTO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Component
@ConfigurationProperties("wechatwork")
public class WeChatWorkCorpProperties {

    private List<AppNotify> apps;

    private Map<String, WeChatCorpInfoDTO> map = new ConcurrentHashMap<>();

    @PostConstruct
    public void initWeChatWorkMap() {
        if (CollectionUtils.isNotEmpty(apps)) {
            for (AppNotify appNotify : apps) {
                WeChatCorpInfoDTO corpInfoDTO = new WeChatCorpInfoDTO(appNotify.getAppCode(), appNotify.getCorpId(), appNotify.getCorpSecret());
                map.putIfAbsent(corpInfoDTO.getAppCode(), corpInfoDTO);
            }
        }
    }

    @Data
    public static class AppNotify {

        /**
         * 应用编码（自定义的）
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

    }
}
