package com.example.plus.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.plus.content.domain.po.ContentPO;
import com.example.plus.content.domain.vo.ContentVO;

/**
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Service
 * @createDate 2023-03-08 17:21:57
 */
public interface ContentService extends IService<ContentPO> {

    void saveContentService(ContentVO vo);
}
