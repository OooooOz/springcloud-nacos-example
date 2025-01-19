package com.example.common.controller;

import com.example.common.model.dto.CommonConfigDTO;
import com.example.common.service.CommonConfigService;
import com.example.model.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用控制层
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Autowired
    private CommonConfigService commonConfigService;

    /**
     * 提交配置
     *
     * @param commonConfigDTO
     *
     * @return
     */
    @PostMapping("/config/submit")
    public BaseResponse submit(@RequestBody @Validated CommonConfigDTO commonConfigDTO) {
        Long id = commonConfigService.submit(commonConfigDTO);
        return BaseResponse.SUCCESS(id);
    }
}
