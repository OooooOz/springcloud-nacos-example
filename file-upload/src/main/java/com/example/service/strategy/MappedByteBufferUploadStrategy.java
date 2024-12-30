package com.example.service.strategy;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.model.dto.FileUploadDTO;
import com.example.service.helper.FileHelper;
import com.example.util.FileUtils;
import com.example.utils.CommonUtil;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("mappedByteBufferUpload")
public class MappedByteBufferUploadStrategy extends SliceUploadTemplate {

    @Autowired
    private FileHelper fileHelper;

    @Value("${upload.chunkSize}")
    private long defaultChunkSize;

    @Override
    public boolean upload(FileUploadDTO param) {

        RandomAccessFile tempRaf = null;
        FileChannel fileChannel = null;
        MappedByteBuffer mappedByteBuffer = null;
        try {
            param.setName(CommonUtil.checkEmptyDefault(param.getName(), param.getFile().getOriginalFilename()));
            String path = fileHelper.getPath(param);
            File tmpFile = super.createTmpFile(param);
            tempRaf = new RandomAccessFile(tmpFile, "rw");
            fileChannel = tempRaf.getChannel();

            long chunkSize = Objects.isNull(param.getChunkSize()) ? defaultChunkSize * 1024 * 1024 : param.getChunkSize();
            // 写入该分片数据
            long offset = chunkSize * param.getChunk();
            byte[] fileData = param.getFile().getBytes();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
            mappedByteBuffer.put(fileData);

            param.setSuccessFile(tmpFile);
            return super.checkAndSetUploadProgress(param, path);

        } catch (Exception e) {
            log.error("upload exception：{}", e);
        } finally {
            FileUtils.freedMappedByteBuffer(mappedByteBuffer);
            IoUtil.close(fileChannel);
            IoUtil.close(tempRaf);
        }

        return false;
    }

}
