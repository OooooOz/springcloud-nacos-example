package com.example.service.strategy;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.model.dto.FileUploadDTO;
import com.example.utils.CommonUtil;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("randomAccessUpload")
public class RandomAccessUploadStrategy extends SliceUploadTemplate {

    @Value("${upload.chunkSize}")
    private long defaultChunkSize;

    @Override
    public boolean upload(FileUploadDTO param) {
        RandomAccessFile accessTmpFile = null;
        try {
            param.setName(CommonUtil.checkEmptyDefault(param.getName(), param.getFile().getOriginalFilename()));
            String path = fileHelper.getPath(param);
            File tmpFile = super.createTmpFile(param);
            accessTmpFile = new RandomAccessFile(tmpFile, "rw");
            // 这个必须与前端设定的值一致
            long chunkSize = Objects.isNull(param.getChunkSize()) ? defaultChunkSize * 1024 * 1024 : param.getChunkSize();
            long offset = chunkSize * param.getChunk();
            // 定位到该分片的偏移量
            accessTmpFile.seek(offset);
            // 写入该分片数据
            accessTmpFile.write(param.getFile().getBytes());
            param.setSuccessFile(tmpFile);
            return super.checkAndSetUploadProgress(param, path);
        } catch (Exception e) {
            log.error("upload exception：{}", e);
        } finally {
            IoUtil.close(accessTmpFile);
        }

        return false;
    }

}
