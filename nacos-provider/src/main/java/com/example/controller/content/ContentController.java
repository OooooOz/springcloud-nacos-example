package com.example.controller.content;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.model.BaseResponse;
import com.example.model.dto.ContentDTO;
import com.example.model.vo.ContentVO;
import com.example.service.ContentService;

@RestController
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/save")
    public BaseResponse saveContentService(Integer count) {
        contentService.saveContentService(count);
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/list/page")
    public BaseResponse findContentServicePage(@RequestBody ContentDTO dto) {
        List<ContentVO> vos = contentService.findContentServicePage(dto);
        return BaseResponse.SUCCESS(vos);
    }

    @GetMapping("/update/{type}")
    public BaseResponse updateBatch(@RequestParam("type") Integer type) {
        contentService.updateBatch(type);
        return BaseResponse.SUCCESS();
    }

    @PutMapping("/update")
    public BaseResponse updateOne(@RequestBody ContentDTO dto) {
        contentService.updateOne(dto);
        return BaseResponse.SUCCESS();
    }

}
