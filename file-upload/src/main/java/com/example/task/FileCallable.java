package com.example.task;

import java.util.concurrent.Callable;

import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;
import com.example.service.strategy.SliceUploadStrategy;

public class FileCallable implements Callable<FileUpload> {

    private SliceUploadStrategy strategy;

    private FileUploadRequest param;

    public FileCallable(SliceUploadStrategy strategy, FileUploadRequest param) {

        this.strategy = strategy;
        this.param = param;
    }

    @Override
    public FileUpload call() {
        return strategy.sliceUpload(param);
    }
}
