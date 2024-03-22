package com.example.service.helper;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.model.vo.FileUpload;

@Component
public class ExecutorHelper {

    @Value("${upload.block.queue-size}")
    private Integer blockQueueSize;

    public CompletionService<FileUpload> getFileUploadCompletionService(Executor fileUploadExecutor) {
        return new ExecutorCompletionService<>(fileUploadExecutor, new LinkedBlockingDeque<>(blockQueueSize));
    }

}
