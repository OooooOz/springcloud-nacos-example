package com.example.cover.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MapperConfig;

import java.util.List;
import java.util.stream.Stream;

@MapperConfig
public interface BaseMapping<SOURCE, TARGET> {

    /**
     * 映射同名属性
     *
     * @param var1
     * @return
     */
    TARGET map(SOURCE var1);

    /**
     * 反向，映射同名属性
     *
     * @param var1
     * @return
     */
    @InheritInverseConfiguration(name = "map")
    SOURCE reversed(TARGET var1);

    /**
     * 映射同名属性，集合形式
     *
     * @param var1
     * @return
     */
    @InheritConfiguration(name = "map")
    List<TARGET> map(List<SOURCE> var1);

    /**
     * 反向，映射同名属性，集合形式
     *
     * @param var1
     * @return
     */
    @InheritConfiguration(name = "reversed")
    List<SOURCE> reversed(List<TARGET> var1);

    /**
     * 映射同名属性，集合流形式
     *
     * @param stream
     * @return
     */
    List<TARGET> map(Stream<SOURCE> stream);

    /**
     * 反向，映射同名属性，集合流形式
     *
     * @param stream
     * @return
     */
    List<SOURCE> reversed(Stream<TARGET> stream);
}
