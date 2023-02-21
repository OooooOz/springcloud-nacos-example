package com.example.util;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.fastjson.util.IOUtils;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * EasyExcel工具类
 **/
@Slf4j
public class EasyExcelUtils {

    /**
     * 导出Excel，可自定义文件名、sheet表名、表头
     *
     * @param fileName  文件名
     * @param sheetName sheet表名
     * @param headClass 表头映射的实体类
     * @param dataList  导出数据的实体类集合
     * @param response
     */
    public static void exportExcel(String fileName, String sheetName, Class headClass, List dataList,
                                   HttpServletResponse response) {
        try {
            // 初始化响应头
            initResponseHeader(fileName, response);

            EasyExcel.write(response.getOutputStream(), headClass).sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出Excel，可自定义文件名、sheet表名、表头
     *
     * @param fileName  文件名
     * @param sheetName sheet表名
     * @param headClass 表头映射的实体类
     * @param dataList  导出数据的实体类集合
     * @param response
     */
    public static void exportExcel(ExcelWriterBuilder excelWriterBuilder, String fileName, String sheetName, Class headClass, List dataList,
                                   HttpServletResponse response) {
        try {
            // 初始化响应头
            initResponseHeader(fileName, response);

            excelWriterBuilder.file(response.getOutputStream());
            if (headClass != null) {
                excelWriterBuilder.head(headClass);
            }
            excelWriterBuilder.sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 排除列导出Excel，可自定义文件名、sheet表名、表头
     *
     * @param fileName         文件名
     * @param sheetName        sheet表名
     * @param headClass        表头映射的实体类
     * @param dataList         导出数据的实体类集合
     * @param excludeColumnSet 排除列集合
     * @param response
     */
    public static void exportExcelByExcludeColumn(String fileName, String sheetName, Class headClass, List dataList,
                                                  Collection<String> excludeColumnSet, HttpServletResponse response) {
        try {
            // 初始化响应头
            initResponseHeader(fileName, response);

            EasyExcel.write(response.getOutputStream(), headClass).excludeColumnFiledNames(excludeColumnSet)
                    .sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定列导出Excel，可自定义文件名、sheet表名、表头
     *
     * @param fileName         文件名
     * @param sheetName        sheet表名
     * @param headClass        表头映射的实体类
     * @param dataList         导出数据的实体类集合
     * @param includeColumnSet 指定列集合
     * @param response
     */
    public static void exportExcelByIncludeColumn(String fileName, String sheetName, Class headClass, List dataList,
                                                  Collection<String> includeColumnSet, HttpServletResponse response) {
        try {
            // 初始化响应头
            initResponseHeader(fileName, response);

            EasyExcel.write(response.getOutputStream(), headClass).includeColumnFiledNames(includeColumnSet)
                    .sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 功能描述：Workbook转MultipartFile
     *
     * @param workbook 工作簿
     * @return
     */
    public static MultipartFile workbook2MultipartFile(Workbook workbook) {
        ByteArrayOutputStream bos = null;
        MultipartFile multipartFile = null;
        try {
            // Workbook 转 MultipartFile
            bos = new ByteArrayOutputStream();
            workbook.write(bos);

            byte[] bytes = bos.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            multipartFile = new MockMultipartFile("error.xlsx", "error.xlsx", MediaType.MULTIPART_FORM_DATA_VALUE, inputStream);
        } catch (IOException e) {
            log.error("Workbook转MultipartFile", e);
        } finally {
            if (bos != null) {
                IoUtil.close(bos);
            }
        }
        return multipartFile;
    }

    /**
     * 导出Excel，可自定义文件名、sheet表名、表头
     *
     * @param fileName  文件名
     * @param sheetName sheet表名
     * @param headClass 表头映射的实体类
     * @param dataList  导出数据的实体类集合
     */
    public static MultipartFile excelWriterBuilder2MultipartFile(ExcelWriterBuilder excelWriterBuilder, String fileName, String sheetName, Class headClass, List dataList) {
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            excelWriterBuilder.file(bos);
            if (headClass != null) {
                excelWriterBuilder.head(headClass);
            }
            excelWriterBuilder.sheet(sheetName).doWrite(dataList);
            byte[] bytes = bos.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, MediaType.MULTIPART_FORM_DATA_VALUE, inputStream);
            return multipartFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化响应头
     *
     * @param fileName 文件名
     * @param response
     */
    private static void initResponseHeader(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    public static File transferToFile(MultipartFile multipartFile) {
        File file;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file = File.createTempFile(UUID.randomUUID().toString(), "." + filename[filename.length - 1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            log.error("文件转换失败");
            throw new RuntimeException("文件转换失败");
        }
        return file;
    }

    public static <T> List<T> getReportDateList(String ossPath, Class<T> clazz) throws IOException {
        URL url = new URL(ossPath);
        InputStream inputStream = url.openStream();
        List<T> date = readExcelWithCSV(inputStream, clazz);
        return date;
    }

    /**
     * 读取Excel数据
     *
     * @param excel csv文件流，使用完自动关闭
     * @param t     csv文件对应的解析实体类，参照com.gerpgo.walmartzt.dto.report包中的实体类
     */
    public static <T> List<T> readExcelWithCSV(InputStream excel, Class<T> t) {
        InputStreamReader is = null;
        List<T> reports = null;
        try {
            is = new InputStreamReader(excel, StandardCharsets.UTF_8);
            reports = new CsvToBeanBuilder<T>(is).withType(t).build().parse();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(is);
        }
        return reports;
    }

}
