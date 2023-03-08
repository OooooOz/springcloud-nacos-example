package com.example.plus.content.controller;

import com.example.plus.content.domain.vo.ContentVO;
import com.example.plus.content.service.ContentService;
import com.example.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/save")
    public BaseResponse saveContentService(@RequestBody ContentVO vo) {
        contentService.saveContentService(vo);
        return BaseResponse.SUCCESS();
    }

}
