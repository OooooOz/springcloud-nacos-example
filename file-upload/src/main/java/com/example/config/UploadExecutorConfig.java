package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class UploadExecutorConfig {

    @Value("${upload.executor.queue-max-size}")
    private Integer maxQueueSize;

    /**
     * 文件上传线程池
     *
     * @return
     */
    @Bean(name = "fileUploadExecutor")
    public Executor fileUploadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 此方法返回可用处理器的虚拟机的最大数量; 不小于1
        int core = Runtime.getRuntime().availableProcessors();
        log.info("可用处理器的虚拟机的最大数量:" + core);
        // 线程池大小
        executor.setCorePoolSize(core);
        // 线程池最大线程数
        executor.setMaxPoolSize(core * 2 + 1);
        // 最大等待任务数 如果传入值大于0，底层队列使用的是LinkedBlockingQueue,否则默认使用SynchronousQueue
        executor.setQueueCapacity(10);
        if (maxQueueSize != null) {
            executor.setQueueCapacity(maxQueueSize);
        }
        // 除核心线程外的线程存活时间
        executor.setKeepAliveSeconds(5);
        executor.setThreadNamePrefix("file-upload-executor");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60 * 15);
        executor.initialize();
        return executor;
    }
}
