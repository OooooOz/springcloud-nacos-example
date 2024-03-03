package com.example.controller.content;

import com.example.domain.vo.ContentVO;
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
    public BaseResponse findContentServicePage(@RequestBody ContentVO vo) {
        List<ContentVO> vos = contentService.findContentServicePage(vo);
        return BaseResponse.SUCCESS(vos);
    }

    @GetMapping("/update/{type}")
    public BaseResponse updateBatch(@RequestParam("type") Integer type) {
        contentService.updateBatch(type);
        return BaseResponse.SUCCESS();
    }

}
