package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.po.Content;
import com.example.model.vo.ContentVO;

/**
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Mapper
 * @createDate 2023-03-08 17:21:57
 * @Entity com.example.plus.cntent.domain.Content
 */
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * caseWhen更新
     *
     * @param contentVOS
     */
    void updateBatchCaseWhen(@Param("list") List<ContentVO> contentVOS);

    /**
     * foreach更新
     *
     * @param contentVOS
     */
    void updateForeach(List<ContentVO> contentVOS);
}
