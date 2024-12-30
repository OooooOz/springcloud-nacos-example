package com.example.service.strategy;

import com.example.model.dto.FileUploadDTO;
import com.example.model.vo.FileUploadVo;

public interface SliceUploadStrategy {

    FileUploadVo sliceUpload(FileUploadDTO param);
}
