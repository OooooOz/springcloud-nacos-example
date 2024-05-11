package com.example.service.strategy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.contant.FileConstant;
import com.example.model.dto.FileUploadRequest;
import com.example.model.vo.FileUpload;
import com.example.service.helper.FilePathHelper;
import com.example.util.FileMD5Util;
import com.example.util.FileUtils;
import com.example.util.RedisUtil;
import com.example.utils.DateUtils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SliceUploadTemplate implements SliceUploadStrategy {

    @Autowired
    protected FilePathHelper filePathHelper;

    @Autowired
    protected RedisUtil redisUtil;

    public abstract boolean upload(FileUploadRequest param);

    protected File createTmpFile(FileUploadRequest param) {
        param.setPath(FileUtils.withoutHeadAndTailDiagonal(param.getPath()));
        String path = filePathHelper.getPath(param);
        FileUtils.checkPath(path);

        String fileName = param.getFile().getOriginalFilename();
        return new File(path, fileName);
    }

    @Override
    public FileUpload sliceUpload(FileUploadRequest param) {

        boolean isOk = this.upload(param);
        if (isOk) {
            File tmpFile = this.createTmpFile(param);
            FileUpload fileUploadDTO = this.saveAndFileUploadDTO(param.getName(), tmpFile);
            return fileUploadDTO;
        }
        String md5 = FileMD5Util.getFileMD5(param.getFile());

        Map<Integer, String> map = new HashMap<>();
        map.put(param.getChunk(), md5);
        return FileUpload.builder().chunkMd5Info(map).build();
    }

    /**
     * 保存文件操作
     */
    public FileUpload saveAndFileUploadDTO(String fileName, File tmpFile) {

        FileUpload fileUploadDTO = null;

        try {

            fileUploadDTO = renameFile(tmpFile, fileName);
            if (fileUploadDTO.isUploadComplete()) {
                System.out.println("upload complete !!" + fileUploadDTO.isUploadComplete() + " name=" + fileName);
                // TODO 保存文件信息到数据库

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {

        }
        return fileUploadDTO;
    }

    /**
     * 文件重命名
     *
     * @param toBeRenamed 将要修改名字的文件
     * @param toFileNewName 新的名字
     */
    private FileUpload renameFile(File toBeRenamed, String toFileNewName) {
        // 检查要重命名的文件是否存在，是否是文件
        FileUpload fileUploadDTO = new FileUpload();
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            log.info("File does not exist: {}", toBeRenamed.getName());
            fileUploadDTO.setUploadComplete(false);
            return fileUploadDTO;
        }
        String ext = FileUtils.getExtension(toFileNewName);
        String p = toBeRenamed.getParent();
        String filePath = p + FileConstant.FILE_SEPARATORCHAR + toFileNewName;
        File newFile = new File(filePath);
        // 修改文件名
        boolean uploadFlag = toBeRenamed.renameTo(newFile);

        fileUploadDTO.setMtime(DateUtils.getCurrentTimeStamp());
        fileUploadDTO.setUploadComplete(uploadFlag);
        fileUploadDTO.setPath(filePath);
        fileUploadDTO.setSize(newFile.length());
        fileUploadDTO.setFileExt(ext);
        fileUploadDTO.setFileId(toFileNewName);

        return fileUploadDTO;
    }

    /**
     * 检查并修改文件上传进度
     */
    public boolean checkAndSetUploadProgress(FileUploadRequest param, String uploadDirPath) {
        String fileName = param.getFile().getOriginalFilename();
        File confFile = new File(uploadDirPath, FileUtil.mainName(fileName) + ".conf");
        byte isComplete = 0;
        RandomAccessFile accessConfFile = null;
        try {
            accessConfFile = new RandomAccessFile(confFile, "rw");
            // 把该分段标记为 true 表示完成
            System.out.println("set part " + param.getChunk() + " complete");
            // 创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认0,已上传的就是Byte.MAX_VALUE 127
            accessConfFile.setLength(param.getChunks());
            accessConfFile.seek(param.getChunk());
            accessConfFile.write(Byte.MAX_VALUE);

            // completeList 检查是否全部完成,如果数组里是否全部都是127(全部分片都成功上传)
            byte[] completeList = FileUtil.readBytes(confFile);
            isComplete = Byte.MAX_VALUE;
            for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
                // 与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
                isComplete = (byte)(isComplete & completeList[i]);
                System.out.println("check part " + i + " complete?:" + completeList[i]);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IoUtil.close(accessConfFile);
        }
        boolean isOk = setUploadProgress2Redis(param, uploadDirPath, fileName, confFile, isComplete);
        return isOk;
    }

    /**
     * 把上传进度信息存进redis
     */
    private boolean setUploadProgress2Redis(FileUploadRequest param, String uploadDirPath, String fileName, File confFile, byte isComplete) {
        if (isComplete == Byte.MAX_VALUE) {
            redisUtil.hset(FileConstant.FILE_UPLOAD_STATUS, param.getMd5(), "true");
            redisUtil.del(FileConstant.FILE_MD5_KEY + param.getMd5());
            confFile.delete();
            return true;
        } else {
            if (!redisUtil.hHasKey(FileConstant.FILE_UPLOAD_STATUS, param.getMd5())) {
                redisUtil.hset(FileConstant.FILE_UPLOAD_STATUS, param.getMd5(), "false");
                redisUtil.set(FileConstant.FILE_MD5_KEY + param.getMd5(), uploadDirPath + FileConstant.FILE_SEPARATORCHAR + fileName + ".conf");
            }

            return false;
        }
    }

}
