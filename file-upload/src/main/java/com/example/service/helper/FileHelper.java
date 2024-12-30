package com.example.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.contant.FileConstant;
import com.example.model.dto.FileUploadDTO;
import com.example.util.SystemUtil;

@Component
public class FileHelper {

    @Value("${upload.root.dir}")
    private String uploadRootDir;

    @Value("${upload.window.root}")
    private String uploadWindowRoot;

    public String getPath() {
        return uploadRootDir;
    }

    public String getBasePath() {
        String path = uploadRootDir;
        if (SystemUtil.isWinOs()) {
            path = uploadWindowRoot + uploadRootDir;
        }

        return path;
    }

    public String getPath(FileUploadDTO param) {
        if (StringUtils.isNotBlank(param.getPath())) {
            return param.getPath();
        } else {
            String path = this.getBasePath() + FileConstant.FILE_SEPARATORCHAR + param.getMd5();
            param.setPath(path);
            return path;
        }
    }

}
