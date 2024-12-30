package com.example.service;

import java.io.IOException;

import com.example.model.dto.FileUploadDTO;
import com.example.model.vo.FileUploadVo;

public interface FileService {

    FileUploadVo upload(FileUploadDTO dto) throws IOException;

    FileUploadVo sliceUpload(FileUploadDTO dto);

    FileUploadVo checkFileMd5(FileUploadDTO dto);

}
