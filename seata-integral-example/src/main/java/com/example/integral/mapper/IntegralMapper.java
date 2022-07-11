package com.example.integral.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IntegralMapper {

    @Insert("insert into seata_integral(id,user_id,integral) values (null,#{userId},#{integral})")
    int addIntegral(@Param("userId") Long userId, @Param("integral") int integral);
}
