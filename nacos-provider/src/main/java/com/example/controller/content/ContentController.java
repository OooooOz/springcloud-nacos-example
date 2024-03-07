package com.example.controller.content;

import com.example.model.dto.ContentDTO;
import com.example.model.vo.ContentVO;
import com.example.response.BaseResponse;
import com.example.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/save")
    public BaseResponse saveContentService() {
        contentService.saveContentService();
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
