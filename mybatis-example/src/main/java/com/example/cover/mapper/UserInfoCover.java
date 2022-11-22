package com.example.cover.mapper;

import com.example.cover.po.JobPO;
import com.example.cover.po.UserInfoPO;
import com.example.cover.vo.JobVO;
import com.example.cover.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserInfoCover {

    UserInfoCover INSTANCE = Mappers.getMapper(UserInfoCover.class);

    @Mappings({
            @Mapping(source = "userName", target = "name")
    })
    UserInfoVO toConvertUserInfoVo(UserInfoPO po);

    @Mapping(source = "jobName", target = "jName")
    JobVO toConvertJobVo(JobPO po);

    @IterableMapping(qualifiedByName = "toConvertUserInfoVoWithNoAge")
    List<UserInfoVO> toConvertUserInfoVos(List<UserInfoPO> pos);

    @Named("toConvertUserInfoVoWithNoAge")
    @Mappings({
            @Mapping(source = "userName", target = "name"),
            @Mapping(source = "age", target = "age", ignore = true)
    })
    UserInfoVO toConvertUserInfoVoWithNoAge(UserInfoPO po);

    @Mapping(source = "userName", target = "name")
    UserInfoVO toConvertAfter(UserInfoPO po);

    @AfterMapping
    default void toConvertAfter(UserInfoPO po, @MappingTarget UserInfoVO.UserInfoVOBuilder vo) {
        vo.name(StringUtils.toRootUpperCase(po.getUserName()));
    }
}