package com.example.pdf;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.pdf.vo.RenovationDocNameEnum;
import com.google.common.collect.Lists;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import cn.hutool.core.io.IoUtil;

/**
 * @Description
 * @Author c-zhongwh01
 * @Date 2023/6/14 14:33
 */
public class PdfUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfUtil.class);
    private static final String DIR = "user.dir";
    private static BaseFont baseFont;
    private static Font font;
    private static Font specialFont;
    private static Font fontBold;
    private static Image image;

    private PdfUtil() {
    }

    public static BaseFont getBaseFont(String fontDir) {
        if (baseFont == null) {
            String fontPath = System.getProperty(DIR) + getResourcePath("/src/main/resources/font", "/simkai.ttf");
            if (StringUtils.isNotBlank(fontDir)) {
                fontPath = System.getProperty(DIR) + getResourcePath("/src/main/resources/font", fontDir);
            }
            try {
                baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, false);
            } catch (DocumentException | IOException e) {
                LOGGER.info("创建字体失败,路径：{}，异常信息：{}", fontPath, e);
            }
            // 创建字体对象
            return baseFont;
        } else {
            return baseFont;
        }
    }

    public static Font getFont(String fontDir) {
        if (font == null) {
            // 创建字体对象
            font = new Font(getBaseFont(fontDir), 9, Font.NORMAL);
            return font;
        } else {
            return font;
        }
    }

    public static Font getSpecialFont(String fontDir) {
        if (specialFont == null) {
            // 创建字体对象
            specialFont = new Font(getBaseFont(fontDir), 5, Font.NORMAL);
            return specialFont;
        } else {
            return specialFont;
        }
    }

    public static Font getBoldFont(String fontDir) {
        if (fontBold == null) {
            // 创建字体对象
            fontBold = new Font(getBaseFont(fontDir), 12, Font.BOLD);
            return fontBold;
        } else {
            return fontBold;
        }
    }

    /**
     * 获取页眉图片 本地开发路径修改为：String imgPath = System.getProperty(DIR) + "/src/main/resources/img/head.png";
     *
     * @return
     */
    public static Image getImg() {
        if (image == null) {
            String imgPath = System.getProperty(DIR) + getResourcePath("/src/main/resources/img", "/head.png");
            try {
                image = Image.getInstance(imgPath);
            } catch (BadElementException | IOException e) {
                LOGGER.info("获取页眉图片失败，路径：{}，异常信息：{}", imgPath, e);
            }
        }
        return image;
    }

    private static String getResourcePath(String prefixPath, String fileName) {
        String path;
        if ("/usr/src/app".equals(System.getProperty(DIR))) {
            path = fileName;
        } else {
            path = prefixPath + fileName;
        }
        return path;
    }

    /**
     * 设置A4横向纸张
     *
     * @param document
     */
    public static void setA4HorizontalPage(Document document) {
        // 横向
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
    }

    /**
     * 表格文字居中
     *
     * @param name      单元格内容
     * @param font      内容字体设置
     * @param alignment
     * @return
     */
    public static PdfPCell getPDFCell(String name, Font font, Integer alignment) {
        PdfPCell cell = new PdfPCell();
        if (name == null) {
            name = " ";
        }
        Paragraph p = new Paragraph(name, font);
        if (alignment == null) {
            p.setAlignment(Element.ALIGN_CENTER);
        } else {
            p.setAlignment(alignment);
        }
        cell.setUseAscender(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(p);
        return cell;
    }

    /**
     * 表格文字居中（含特殊字符）
     *
     * @param name      单元格内容
     * @param font      内容字体设置
     * @param alignment
     * @return
     */
    public static PdfPCell getPDFCellSpecial(String name, Font font, Integer alignment) {
        PdfPCell cell = new PdfPCell();
        if (name == null) {
            name = " ";
        }
        Paragraph p = new Paragraph(name, font);
        Font specialFont = PdfUtil.getSpecialFont(null);
        Chunk chunk = new Chunk("2", specialFont);
        chunk.setTextRise(5f);
        p.add(chunk);
        p.add(new Chunk("）", font));

        if (alignment == null) {
            p.setAlignment(Element.ALIGN_CENTER);
        } else {
            p.setAlignment(alignment);
        }
        cell.setUseAscender(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.addElement(p);
        return cell;
    }

    /**
     * 单行合并单元格返回单元格
     *
     * @param name      单元格内容
     * @param font      内容字体设置
     * @param alignment 居中格式
     * @param colSpan   合并到哪一列
     * @return
     */
    public static PdfPCell getPDFCellWithMerge(String name, Font font, Integer alignment, int colSpan) {
        PdfPCell pCell = getPDFCell(name, font, alignment);
        pCell.setRowspan(1);
        pCell.setColspan(colSpan);
        pCell.setMinimumHeight(25);// SUPPRESS
        return pCell;
    }

    public static String checkPdfPath(String path) {
        String pdfPath = System.getProperty(DIR) + path;
        File pdfFolder = new File(pdfPath);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        return pdfPath;
    }

    public static Document getA4DocumentWithBusinessType(String type) {
        Document document = new Document(PageSize.A4, 0, 0, 50, 0);
        PdfName businessType = new PdfName("businessType");
        document.setAccessibleAttribute(businessType, new PdfString(type));
        return document;
    }

    /* 合并pdf文件
     * @param files 要合并文件数组(绝对路劲{ "D:\\a.pdf", "D:\\b.pdf" })
     * @param savePath 合并后新产生的文件绝对路径如D:\\temp.pdf
     */
    public static Integer mergePdfFiles(String[] files, String savePath) {
        Document document = null;
        PdfCopy copy = null;
        try (FileOutputStream outputStream = new FileOutputStream(savePath)) {
            // 创建一个与a.pdf相同纸张大小的document
            document = new Document(new PdfReader(files[0]).getPageSize(1));
            copy = new PdfCopy(document, outputStream);
            document.open();
            for (int i = 0; i < files.length; i++) {
                // 一个一个的遍历现有的PDF
                dealFile(files[i], copy, document);
            }
            copy.close();
            document.close();
            return 1;
        } catch (IOException | DocumentException e) {
            LOGGER.info("合并pdf失败，异常信息：{}", e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (copy != null) {
                copy.close();
            }
        }
        return 0;
    }

    private static void dealFile(String file, PdfCopy copy, Document document) {
        PdfReader reader = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            reader = new PdfReader(inputStream);
            int n = reader.getNumberOfPages();// PDF文件总共页数
            for (int j = 1; j <= n; j++) {
                document.newPage();
                PdfImportedPage page = copy.getImportedPage(reader, j);
                copy.addPage(page);
            }
            reader.close();
        } catch (IOException | BadPdfFormatException e) {
            throw new ExceptionConverter(e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }


    /**
     * 转为base64编码
     */
    public static String getBase64(byte[] buffer) {
        return Base64.getEncoder().encodeToString(buffer);
    }


    private static List<String> extractAllByInputStream(InputStream inputStream, String pdfPath) {
        ArrayList<String> list = Lists.newArrayList();
        try {
            dealZipInputStream(inputStream, pdfPath, list);
        } catch (IOException e) {
            LOGGER.info("下载pdf到本地异常，异常信息：{}", e);
        }

        return list;
    }

    private static void dealZipInputStream(InputStream inputStream, String pdfPath, ArrayList<String> list) throws IOException {
        byte[] buffer = new byte[1024];
        ZipEntry zipEntry;
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    String fileName;
                    if (entryName.contains(RenovationDocNameEnum.CB.getName())) {
                        fileName = pdfPath + RenovationDocNameEnum.CB.getName() + ".pdf";
                    } else if (entryName.contains(RenovationDocNameEnum.JC.getName())) {
                        fileName = pdfPath + RenovationDocNameEnum.JC.getName() + ".pdf";
                    } else if (entryName.contains(RenovationDocNameEnum.SG.getName())) {
                        fileName = pdfPath + RenovationDocNameEnum.SG.getName() + ".pdf";
                    } else {
                        fileName = pdfPath + UUID.randomUUID() + ".pdf";
                    }
                    list.add(fileName);
                    File subFile = new File(fileName);
                    subFile.createNewFile();
                    readSubFile(subFile, zipInputStream, buffer);

                }
                zipInputStream.closeEntry();
            }
        } finally {
            IoUtil.close(inputStream);
        }
    }

    private static void readSubFile(File subFile, ZipInputStream zipInputStream, byte[] buffer) {
        int len;
        try (FileOutputStream fileOut = new FileOutputStream(subFile)) {
            while ((len = zipInputStream.read(buffer)) > 0) {
                fileOut.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

}
