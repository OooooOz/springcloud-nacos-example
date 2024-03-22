package com.example.service.strategy;

import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;

public interface SliceUploadStrategy {

    FileUpload sliceUpload(FileUploadRequest param);
}
