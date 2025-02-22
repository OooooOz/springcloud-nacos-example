package com.example;

import com.alibaba.fastjson.JSON;
import com.example.config.WeChatWorkCorpProperties;
import com.example.log.model.KeyLogHelper;
import com.example.log.model.bo.KeyLogBo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

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

    @Test
    public void testKeyLog() {
        KeyLogHelper.log("没有格式化参数");
        KeyLogHelper.log("有格式化参数, param1：{}, param2：{}", "AAA", "BBB");
        KeyLogHelper.dbLog("入库入参json", "保存日志成功");

        KeyLogBo keyLogBo = new KeyLogBo(true).setModule("user").setFunc("save").setRepeatKey("userId").setParam("param");
        KeyLogHelper.log(keyLogBo, "保存用户成功");

    }
}
