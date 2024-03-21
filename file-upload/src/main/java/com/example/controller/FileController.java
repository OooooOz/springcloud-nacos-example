package com.example.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.dto.FileDownloadRequest;
import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;
import com.example.response.BaseResponse;
import com.example.service.FileService;
import com.example.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @PostMapping(value = "/upload")
    @ResponseBody
    public BaseResponse<FileUpload> upload(FileUploadRequest fileUploadRequestDTO) throws IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        FileUpload fileUploadDTO = null;
        if (isMultipart) {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start("upload");
            if (fileUploadRequestDTO.getChunk() != null && fileUploadRequestDTO.getChunks() > 0) {
                fileUploadDTO = fileService.sliceUpload(fileUploadRequestDTO);
            } else {
                fileUploadDTO = fileService.upload(fileUploadRequestDTO);
            }
            stopWatch.stop();
            log.info("{}", stopWatch.prettyPrint());

            return BaseResponse.SUCCESS(fileUploadDTO);
        }

        throw new RuntimeException("上传失败");

    }

    @RequestMapping(value = "checkFileMd5", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<FileUpload> checkFileMd5(String md5, String path) throws IOException {

        FileUploadRequest param = new FileUploadRequest().setPath(path).setMd5(md5);
        FileUpload fileUploadDTO = fileService.checkFileMd5(param);

        return BaseResponse.SUCCESS(fileUploadDTO);
    }

    @PostMapping("/download")
    public void download(FileDownloadRequest requestDTO) {

        try {
            FileUtil.downloadFile(requestDTO.getName(), requestDTO.getPath(), request, response);
        } catch (FileNotFoundException e) {
            log.error("download error:" + e.getMessage(), e);
            throw new RuntimeException("文件下载失败");
        }
    }

}
