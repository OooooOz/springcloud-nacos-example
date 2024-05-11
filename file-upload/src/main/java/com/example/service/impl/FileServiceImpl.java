package com.example.service.impl;

import cn.hutool.core.io.IoUtil;
import com.example.contant.FileConstant;
import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;
import com.example.service.FileService;
import com.example.service.helper.ExecutorHelper;
import com.example.service.helper.FilePathHelper;
import com.example.service.strategy.SliceUploadFactory;
import com.example.service.strategy.SliceUploadStrategy;
import com.example.task.FileCallable;
import com.example.util.FileMD5Util;
import com.example.util.FileUtils;
import com.example.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FilePathHelper filePathHelper;

    @Resource(name = "fileUploadExecutor")
    private Executor executor;

    @Autowired
    private ExecutorHelper executorHelper;

    @Value("${upload.mode}")
    private String mode;

    @Autowired
    private SliceUploadFactory sliceUploadFactory;

    @Override
    public FileUpload upload(FileUploadRequest param) throws IOException {

        if (Objects.isNull(param.getFile())) {
            throw new RuntimeException("file can not be empty");
        }
        param.setPath(FileUtils.withoutHeadAndTailDiagonal(param.getPath()));
        String md5 = FileMD5Util.getFileMD5(param.getFile());

        param.setMd5(md5);

        String filePath = filePathHelper.getPath(param);
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

        return FileUpload.builder().uploadComplete(true).build();
    }

    @Override
    public FileUpload sliceUpload(FileUploadRequest fileUploadRequestDTO) {
        SliceUploadStrategy strategy = sliceUploadFactory.getStrategyByMode(mode);
        CompletionService<FileUpload> completionService = executorHelper.getFileUploadCompletionService(executor);
        completionService.submit(new FileCallable(strategy, fileUploadRequestDTO));
        try {
            FileUpload fileUploadDTO = completionService.take().get();
            return fileUploadDTO;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileUpload checkFileMd5(FileUploadRequest fileUploadRequestDTO) throws IOException {
        return null;
    }

}
