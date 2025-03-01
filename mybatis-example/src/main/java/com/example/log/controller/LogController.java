package com.example.log.controller;

import com.example.log.model.GiantFileParser;
import com.example.log.model.dto.GiantFileParseDTO;
import com.example.model.BaseResponse;
import com.example.model.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    /**
     * 大文件解析入库
     *
     * @return
     */
    @PostMapping("/giant/parse")
    public BaseResponse submit(@RequestBody @Validated GiantFileParseDTO dto) {
        try {
            if (dto.getType() == 1) {
                // 多线程解析，多线程入库
                GiantFileParser.giantParse(dto);
            } else {
                // 单线程解析，多线程入库
                GiantFileParser.giantParseV2(dto);
            }

        } catch (Exception e) {
            log.info("giantParse exception ：{}", e.getMessage());
            throw BusinessException.failMsg("系统异常");
        }
        return BaseResponse.SUCCESS();
    }
}
