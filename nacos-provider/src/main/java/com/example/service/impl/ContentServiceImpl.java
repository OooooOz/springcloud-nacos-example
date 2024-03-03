package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.domain.po.Content;
import com.example.domain.vo.ContentVO;
import com.example.mapper.ContentMapper;
import com.example.service.ContentService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Service实现
 * @createDate 2023-03-08 17:21:57
 */
@Slf4j
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Override
    public void saveContentService() {
        List<ContentVO> contentVOS = this.buildTestDate();
        List<Content> saves = contentVOS.stream().map(contentVO -> {
            Content po = BeanUtil.copyProperties(contentVO, Content.class);
            po.setSubTitle(null);
            return po;
        }).collect(Collectors.toList());
        this.saveBatch(saves);
    }

    @Override
    public List<ContentVO> findContentServicePage(ContentVO vo) {
        Page<Content> page = new Page<Content>();
        Page<Content> poPage = lambdaQuery().page(page);
        List<Content> records = poPage.getRecords();
        List<ContentVO> vos = BeanUtil.copyToList(records, ContentVO.class);
        return vos;
    }

    @Override
    public void updateBatch(Integer type) {
        List<ContentVO> contentVOS = this.buildTestDate();
        long start = System.currentTimeMillis();
        if (type == 1) {
            baseMapper.updateBatchCaseWhen(contentVOS);
        } else if (type == 2) {
            baseMapper.updateForeach(contentVOS);
        }
        log.info("更新耗时：{}", (System.currentTimeMillis() - start));
    }

    private List<ContentVO> buildTestDate() {
        List<ContentVO> list = Lists.newArrayList();
        long beginId = 1000L;
        int batchSize = 1000;
        for (int i = 0; i < batchSize; i++) {
            ContentVO contentVO = new ContentVO();
            contentVO.setId(beginId + 1);
            contentVO.setName("name-" + i);
            contentVO.setSubTitle("subTilte" + i);
            list.add(contentVO);
        }
        return list;
    }
}
