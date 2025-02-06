package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleEnableBeanFactoryPostProcessorConfig {

    @ConditionalOnProperty(prefix = "schedule", name = "enabled", havingValue = "false")
    @Bean
    public ScheduleEnableRegistryBeanFactoryPostProcessor customRegistryBeanFactoryPostProcessor() {
        return new ScheduleEnableRegistryBeanFactoryPostProcessor();
    }

}
