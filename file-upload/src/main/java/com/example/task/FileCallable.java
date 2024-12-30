package com.example.task;

import java.util.concurrent.Callable;

import com.example.model.dto.FileUploadDTO;
import com.example.model.vo.FileUploadVo;
import com.example.service.strategy.SliceUploadStrategy;

public class FileCallable implements Callable<FileUploadVo> {

    private SliceUploadStrategy strategy;

    private FileUploadDTO param;

    public FileCallable(SliceUploadStrategy strategy, FileUploadDTO param) {
        this.strategy = strategy;
        this.param = param;
    }

    @Override
    public FileUploadVo call() {
        return strategy.sliceUpload(param);
    }
}
