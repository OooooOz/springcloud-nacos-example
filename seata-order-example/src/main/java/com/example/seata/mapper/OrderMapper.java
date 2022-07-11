package com.example.seata.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderMapper {

    @Insert("insert into seata_order(id,user_id,order_status) values (null,10,#{status})")
    int addOrder(@Param("status") String status);

    @Update(value = "update seata_order set order_status = #{status} where id = #{id}")
    int confirmOrder(@Param("id") Long id, @Param("status") String status);
}
