package com.example.cover.mapper;

import com.example.cover.po.UserInfoPO;
import com.example.cover.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserInfoAssembler extends BaseMapping<UserInfoPO, UserInfoVO>{

    UserInfoAssembler INSTANCE = Mappers.getMapper(UserInfoAssembler.class);

}
