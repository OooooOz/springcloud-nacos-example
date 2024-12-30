package com.example.service.strategy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.contant.FileConstant;
import com.example.model.dto.FileUploadDTO;
import com.example.model.vo.FileUploadVo;
import com.example.service.helper.FileHelper;
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
    protected FileHelper fileHelper;

    @Autowired
    protected RedisUtil redisUtil;

    public abstract boolean upload(FileUploadDTO param);

    protected File createTmpFile(FileUploadDTO param) {
        param.setPath(FileUtils.withoutHeadAndTailDiagonal(param.getPath()));
        String path = fileHelper.getPath(param);
        FileUtils.checkPath(path);

        String fileName = param.getName();
        return new File(path, fileName);
    }

    @Override
    public FileUploadVo sliceUpload(FileUploadDTO param) {
        String md5 = FileMD5Util.getFileMD5(param.getFile());
        Map<Integer, String> map = new HashMap<>();
        map.put(param.getChunk(), md5);
        // 文件上传
        boolean isOk = this.upload(param);
        if (isOk) {
            File file = param.getSuccessFile();
            FileUploadVo fileUploadVo = this.buildFileUploadVo(file, param.getName());
            fileUploadVo.setChunkMd5Info(map);
            // 后置文件操作
            this.uploadSuccessPostProcessor(file, fileUploadVo);
            return fileUploadVo;
        }

        return FileUploadVo.builder().chunkMd5Info(map).build();
    }

    private void uploadSuccessPostProcessor(File file, FileUploadVo fileUploadVo) {
        if (!fileUploadVo.isUploadComplete()) {
            log.info("upload not complete !!" + fileUploadVo.isUploadComplete() + " name=" + file.getName());
        }
        // todo 上传oss，替换响应path，保存文件信息到数据库
    }

    /**
     *
     * @param file 完成上传的文件
     * @param fileName
     * @return
     */
    private FileUploadVo buildFileUploadVo(File file, String fileName) {
        FileUploadVo fileUploadVo = new FileUploadVo();
        if (!file.exists() || file.isDirectory()) {
            log.info("File does not exist: {}", file.getName());
            fileUploadVo.setUploadComplete(false);
            return fileUploadVo;
        }
        String ext = FileUtil.getSuffix(fileName);
        fileUploadVo.setMtime(DateUtils.getCurrentTimeStamp());
        fileUploadVo.setUploadComplete(true);
        fileUploadVo.setPath(file.getPath());
        fileUploadVo.setSize(file.length());
        fileUploadVo.setFileExt(ext);
        fileUploadVo.setFileId(fileName);
        return fileUploadVo;
    }

    /**
     * 文件重命名
     *
     * @param toBeRenamed 将要修改名字的文件
     * @param toFileNewName 新的名字
     */
    private FileUploadVo renameFile(File toBeRenamed, String toFileNewName) {
        // 检查要重命名的文件是否存在，是否是文件
        FileUploadVo fileUploadVo = new FileUploadVo();
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            log.info("File does not exist: {}", toBeRenamed.getName());
            fileUploadVo.setUploadComplete(false);
            return fileUploadVo;
        }
        String ext = FileUtil.getSuffix(toFileNewName);
        String p = toBeRenamed.getParent();
        String filePath = p + FileConstant.FILE_SEPARATORCHAR + toFileNewName;
        File newFile = new File(filePath);
        // 修改文件名
        boolean uploadFlag = toBeRenamed.renameTo(newFile);
        fileUploadVo.setMtime(DateUtils.getCurrentTimeStamp());
        fileUploadVo.setUploadComplete(uploadFlag);
        fileUploadVo.setPath(filePath);
        fileUploadVo.setSize(newFile.length());
        fileUploadVo.setFileExt(ext);
        fileUploadVo.setFileId(toFileNewName);

        return fileUploadVo;
    }

    /**
     * 检查并修改文件上传进度
     */
    public boolean checkAndSetUploadProgress(FileUploadDTO param, String uploadDirPath) {
        String fileName = param.getName();
        File confFile = new File(uploadDirPath, FileUtil.mainName(fileName) + ".conf");
        byte isComplete = 0;
        RandomAccessFile accessConfFile = null;
        try {
            accessConfFile = new RandomAccessFile(confFile, "rw");
            // 把该分段标记为 true 表示完成
            log.info("set part " + param.getChunk() + " complete");
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
                log.info("check part " + i + " complete?:" + completeList[i]);
            }
            log.info("part " + param.getChunk() + " isComplete：" + isComplete);
        } catch (IOException e) {
            log.error("checkAndSetUploadProgress exception：{}", e);
        } finally {
            IoUtil.close(accessConfFile);
        }
        boolean isOk = setUploadProgress2Redis(param, uploadDirPath, fileName, confFile, isComplete);
        return isOk;
    }

    /**
     * 把上传进度信息存进redis
     */
    private boolean setUploadProgress2Redis(FileUploadDTO param, String uploadDirPath, String fileName, File confFile, byte isComplete) {
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
