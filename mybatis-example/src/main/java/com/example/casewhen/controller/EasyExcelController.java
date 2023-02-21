package com.example.casewhen.controller;

import com.example.casewhen.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/easyexcel")
public class EasyExcelController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/test")
    public void testSimpleExport(HttpServletResponse response) {
        sysUserService.testSimpleExport(response);
    }
}
