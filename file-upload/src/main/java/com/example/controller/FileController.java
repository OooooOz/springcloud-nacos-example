package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.model.BaseResponse;
import com.example.model.dto.FileDownloadRequest;
import com.example.model.dto.FileUploadDTO;
import com.example.model.dto.FileUploadParamDTO;
import com.example.model.vo.FileUploadVo;
import com.example.service.FileService;
import com.example.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @PostMapping("/upload/big")
    public BaseResponse<FileUploadVo> upload(FileUploadParamDTO fileUploadParamDTO) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("upload");

        BaseResponse<FileUploadVo> res;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            FileUploadVo fileUploadVo;
            FileUploadDTO dto = BeanUtil.copyProperties(fileUploadParamDTO, FileUploadDTO.class);
            if (dto.getChunk() != null && dto.getChunks() > 0) {
                fileUploadVo = fileService.sliceUpload(dto);
            } else {
                fileUploadVo = fileService.upload(dto);
            }
            res = BaseResponse.SUCCESS(fileUploadVo);
        } else {
            res = BaseResponse.FAILURE("非文件上传，请求失败");
        }

        stopWatch.stop();
        log.info("文件上传结束：{}", stopWatch.prettyPrint());
        return res;
    }

    @GetMapping("/checkFileMd5")
    public BaseResponse<FileUploadVo> checkFileMd5(@RequestParam("md5") String md5, @RequestParam(value = "path", required = false) String path) {
        FileUploadDTO param = new FileUploadDTO().setPath(path).setMd5(md5);
        FileUploadVo fileUploadVo = fileService.checkFileMd5(param);
        return BaseResponse.SUCCESS(fileUploadVo);
    }

    @PostMapping("/download")
    public void download(FileDownloadRequest requestDTO) {
        try {
            FileUtils.downloadFile(requestDTO.getName(), requestDTO.getPath(), request, response);
        } catch (FileNotFoundException e) {
            log.error("download error:" + e.getMessage(), e);
            throw new RuntimeException("文件下载失败");
        }
    }

}
