package com.example.service.strategy;

import cn.hutool.core.io.IoUtil;
import com.example.model.dto.FileUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

@Slf4j
@Component("RANDOM_ACCESS")
public class RandomAccessUploadStrategy extends SliceUploadTemplate {

    @Value("${upload.chunkSize}")
    private long defaultChunkSize;

    @Override
    public boolean upload(FileUploadRequest param) {
        RandomAccessFile accessTmpFile = null;
        try {
            String path = filePathHelper.getPath(param);
            File tmpFile = super.createTmpFile(param);
            accessTmpFile = new RandomAccessFile(tmpFile, "rw");
            // 这个必须与前端设定的值一致
            long chunkSize = Objects.isNull(param.getChunkSize()) ? defaultChunkSize * 1024 * 1024 : param.getChunkSize();
            long offset = chunkSize * param.getChunk();
            // 定位到该分片的偏移量
            accessTmpFile.seek(offset);
            // 写入该分片数据
            accessTmpFile.write(param.getFile().getBytes());
            boolean isOk = super.checkAndSetUploadProgress(param, path);
            param.setTempFile(tmpFile);
            return isOk;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IoUtil.close(accessTmpFile);
        }

        return false;
    }

}
