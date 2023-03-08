package com.example.plus.content.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.plus.content.domain.po.ContentPO;

/**
 *
 * @description 针对表【t_content(内容服务配置 )】的数据库操作Mapper
 * @createDate 2023-03-08 17:21:57
 * @Entity com.example.plus.cntent.domain.Content
 */
@Mapper
public interface ContentMapper extends BaseMapper<ContentPO> {

}
