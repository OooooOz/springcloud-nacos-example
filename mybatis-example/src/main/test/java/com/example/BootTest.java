package com.example;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.example.config.WeChatWorkCorpProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MybatisExampleApplication.class})
public class BootTest {

    @Resource
    private WeChatWorkCorpProperties weChatWorkCorpProperties;

    @Test
    public void testWeChatWorkCorpProperties() {
        log.info("weChatWorkCorpProperties map：{}", JSON.toJSONString(weChatWorkCorpProperties.getMap()));
        log.info("weChatWorkCorpProperties key：{}", JSON.toJSONString(weChatWorkCorpProperties.getMap().get("appCode111")));
    }
}
