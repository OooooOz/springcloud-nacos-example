package com.example.service;

import java.io.IOException;

import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;

public interface FileService {

    FileUpload upload(FileUploadRequest fileUploadRequestDTO) throws IOException;

    FileUpload sliceUpload(FileUploadRequest fileUploadRequestDTO);

    FileUpload checkFileMd5(FileUploadRequest fileUploadRequestDTO) throws IOException;

}
