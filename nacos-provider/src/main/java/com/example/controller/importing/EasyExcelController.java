package com.example.controller.importing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/easyexcel")
public class EasyExcelController {

    @GetMapping("/test")
    public void testSimpleExport(HttpServletResponse response) {

    }
}
