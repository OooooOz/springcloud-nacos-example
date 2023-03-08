package com.example.plus.content.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.plus.content.domain.po.ContentPO;
import com.example.plus.content.domain.vo.ContentVO;
import com.example.plus.content.mapper.ContentMapper;
import com.example.plus.content.service.ContentService;

import cn.hutool.core.bean.BeanUtil;

/**
 * 
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Service实现
 * @createDate 2023-03-08 17:21:57
 */
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, ContentPO> implements ContentService {

    @Override
    public void saveContentService(ContentVO vo) {
        ContentPO po = BeanUtil.copyProperties(vo, ContentPO.class);
        save(po);
    }
}
