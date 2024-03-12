package com.example.config;

import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * @Description: Mybatis配置类
 */
@Configuration
@MapperScan(basePackages = "com.example.mapper")
public class MybatisConfig {

    // 当前环境
    @Value("${spring.profiles.active:default}")
    private String profile;

    /**
     * 自定义MyBatis的配置规则；给容器中添加一个ConfigurationCustomizer；
     *
     * @return
     */
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.setCacheEnabled(Boolean.TRUE);
            configuration.setLazyLoadingEnabled(Boolean.TRUE);
            configuration.setAggressiveLazyLoading(Boolean.TRUE);
            configuration.setUseGeneratedKeys(Boolean.TRUE);
            configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
            configuration.setMapUnderscoreToCamelCase(Boolean.TRUE);// 开启驼峰映射
            configuration.setLogImpl(StdOutImpl.class);
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
