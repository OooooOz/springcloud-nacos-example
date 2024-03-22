package com.example.util;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.contant.FileConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtils {

    public static final String PATH_HEAD = "/vagrant/";
    public static final String USER_HEAD = "/data/User";
    public static final String DOT = ".";
    public static final String SLASH_ONE = "/";
    public static final String SLASH_TWO = "\\";
    public static final String HOME = "";
    private static String uploadWindowRoot;

    public static void downloadFile(String name, String path, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        File downloadFile = new File(path);
        String fileName = name;
        if (StringUtils.isBlank(fileName)) {
            fileName = downloadFile.getName();
        }
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);
        response.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // 获取文件大小
        long downloadSize = downloadFile.length();
        long fromPos = 0, toPos = 0;
        if (request.getHeader("Range") == null) {
            response.addHeader(HttpHeaders.CONTENT_LENGTH, downloadSize + "");
        } else {
            log.info("range:{}", response.getHeader("Range"));
            // 如果为持续下载
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            String range = request.getHeader("Range");
            String bytes = range.replaceAll("bytes=", "");
            String[] ary = bytes.split("-");
            fromPos = Long.parseLong(ary[0]);
            log.info("fronPos:{}", fromPos);
            if (ary.length == 2) {
                toPos = Long.parseLong(ary[1]);
            }
            int size;
            if (toPos > fromPos) {
                size = (int)(toPos - fromPos);
            } else {
                size = (int)(downloadSize - fromPos);
            }
            response.addHeader(HttpHeaders.CONTENT_LENGTH, size + "");
            downloadSize = size;
        }

        try (RandomAccessFile in = new RandomAccessFile(downloadFile, "rw"); OutputStream out = response.getOutputStream()) {
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            int bufLen = (int)(downloadSize < 2048 ? downloadSize : 2048);
            byte[] buffer = new byte[bufLen];
            int num;
            // 当前写入客户端大小
            int count = 0;
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
                count += num;
                if (downloadSize - count < bufLen) {
                    bufLen = (int)(downloadSize - count);
                    if (bufLen == 0) {
                        break;
                    }
                    buffer = new byte[bufLen];
                }
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error("download error:" + e.getMessage(), e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 去除首尾斜杠 path
     */
    public static String withoutHeadAndTailDiagonal(String path) {

        int start = 0;
        int end = 0;
        boolean existHeadDiagonal = path.startsWith(FileConstant.FILE_SEPARATORCHAR);
        boolean existTailDiagonal = path.endsWith(FileConstant.FILE_SEPARATORCHAR);
        if (existHeadDiagonal && existTailDiagonal) {
            start = StringUtils.indexOf(path, FileConstant.FILE_SEPARATORCHAR, 0) + 1;
            end = StringUtils.lastIndexOf(path, FileConstant.FILE_SEPARATORCHAR);
            return StringUtils.substring(path, start, end);
        } else if (existHeadDiagonal && !existTailDiagonal) {
            start = StringUtils.indexOf(path, FileConstant.FILE_SEPARATORCHAR, 0) + 1;
            return StringUtils.substring(path, start);
        } else if (!existHeadDiagonal && existTailDiagonal) {
            end = StringUtils.lastIndexOf(path, FileConstant.FILE_SEPARATORCHAR);
            return StringUtils.substring(path, 0, end);
        }
        return path;
    }

    /**
     * 获取扩展名
     */
    public static String getExtension(String fileName) {

        if (StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(fileName, DOT)) {
            return StringUtils.EMPTY;
        }
        String ext = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, DOT) + 1);
        return StringUtils.trimToEmpty(ext);
    }

    public static String checkPath(String path) {
        File pdfFolder = new File(path);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        return path;
    }
}
