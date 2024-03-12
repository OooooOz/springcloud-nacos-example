package com.example.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ContentMapper;
import com.example.model.dto.ContentDTO;
import com.example.model.po.Content;
import com.example.model.vo.ContentVO;
import com.example.service.ContentService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Service实现
 * @createDate 2023-03-08 17:21:57
 */
@Slf4j
@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Override
    public void saveContentService(Integer count) {
        List<ContentVO> contentVOS = this.buildTestDate(count);
        List<Content> saves = contentVOS.stream().map(contentVO -> {
            Content po = BeanUtil.copyProperties(contentVO, Content.class);
            po.setSubTitle(null);
            return po;
        }).collect(Collectors.toList());
        this.saveBatch(saves);
    }

    @Override
    public List<ContentVO> findContentServicePage(ContentDTO dto) {
        Page<Content> page = new Page<Content>();
        Page<Content> poPage = lambdaQuery().page(page);
        List<Content> records = poPage.getRecords();
        List<ContentVO> vos = BeanUtil.copyToList(records, ContentVO.class);
        return vos;
    }

    @Override
    public void updateBatch(Integer type) {
        List<ContentVO> contentVOS = this.buildTestDate(1000);
        long start = System.currentTimeMillis();
        if (type == 1) {
            baseMapper.updateBatchCaseWhen(contentVOS);
        } else if (type == 2) {
            baseMapper.updateForeach(contentVOS);
        }
        log.info("更新耗时：{}", (System.currentTimeMillis() - start));
    }

    private List<ContentVO> buildTestDate(Integer batchSize) {
        List<ContentVO> list = Lists.newArrayList();
        Content one = lambdaQuery().orderByDesc(Content::getId).last("limit 1").one();
        long beginId = one == null ? 0 : one.getId();
        for (int i = 0; i < batchSize; i++) {
            ContentVO contentVO = new ContentVO();
            contentVO.setName("name-" + i);
            contentVO.setSubTitle("subTilte1000" + beginId);
            list.add(contentVO);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(ContentDTO dto) {
        Content content = this.getById(dto.getId());
        Preconditions.checkNotNull(content, "id不正确");
        if (dto.getOverTime() != null) {
            try {
                TimeUnit.SECONDS.sleep(dto.getOverTime());
            } catch (InterruptedException e) {
                log.info("超时异常,e:{}", e.getMessage());
            }
        }
        Content update = new Content();
        update.setId(content.getId());
        update.setSubTitle(dto.getSubTitle());
        this.updateById(update);
    }
}
