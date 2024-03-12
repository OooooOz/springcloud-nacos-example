package com.example.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.dto.ContentDTO;
import com.example.model.po.Content;
import com.example.model.vo.ContentVO;

/**
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Service
 * @createDate 2023-03-08 17:21:57
 */
public interface ContentService extends IService<Content> {

    void saveContentService(Integer count);

    List<ContentVO> findContentServicePage(ContentDTO dto);

    void updateBatch(Integer type);

    void updateOne(ContentDTO dto);
}
