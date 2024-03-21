package com.example.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.contant.FileConstant;
import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;
import com.example.service.FileService;
import com.example.util.FileMD5Util;
import com.example.util.FilePathUtil;
import com.example.util.FileUtil;
import com.example.util.RedisUtil;
import com.example.utils.DateUtils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FilePathUtil filePathUtil;

    @Override
    public FileUpload upload(FileUploadRequest param) throws IOException {

        if (Objects.isNull(param.getFile())) {
            throw new RuntimeException("file can not be empty");
        }
        param.setPath(FileUtil.withoutHeadAndTailDiagonal(param.getPath()));
        String md5 = FileMD5Util.getFileMD5(param.getFile());

        param.setMd5(md5);

        String filePath = filePathUtil.getPath(param);
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String path = filePath + FileConstant.FILE_SEPARATORCHAR + param.getFile().getOriginalFilename();
        FileOutputStream out = new FileOutputStream(path);
        out.write(param.getFile().getBytes());
        out.flush();
        IoUtil.close(out);

        redisUtil.hset(FileConstant.FILE_UPLOAD_STATUS, md5, "true");

        return FileUpload.builder().path(path).mtime(DateUtils.getCurrentTimeStamp()).uploadComplete(true).build();
    }

    @Override
    public FileUpload sliceUpload(FileUploadRequest fileUploadRequestDTO) {
        return null;
    }

    @Override
    public FileUpload checkFileMd5(FileUploadRequest fileUploadRequestDTO) throws IOException {
        return null;
    }

}
