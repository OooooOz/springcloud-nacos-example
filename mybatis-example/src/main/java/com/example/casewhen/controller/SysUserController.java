package com.example.casewhen.controller;

import com.example.casewhen.po.SysUser;
import com.example.casewhen.service.SysUserService;
import com.example.response.BaseResponse;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/casewhen/update")
    public BaseResponse updateBatchCaseWhen() {
        ArrayList<SysUser> list = buildTestDate();
        sysUserService.updateBatchCaseWhen(list);
        return BaseResponse.SUCCESS();
    }

    @GetMapping("/foreach/update")
    public BaseResponse updateForeach() {
        ArrayList<SysUser> list = buildTestDate();
        sysUserService.updateForeach(list);
        return BaseResponse.SUCCESS();
    }

    private ArrayList<SysUser> buildTestDate() {
        ArrayList<SysUser> list = Lists.newArrayList();
        long beginId = 1000L;
        int batchSize = 1000;
        for (int i = 0; i < batchSize; i++) {
            SysUser user = new SysUser();
            user.setDesc("CaseWhen-" + i);
            user.setId(beginId + 1);
            list.add(user);
        }
        return list;
    }

}


