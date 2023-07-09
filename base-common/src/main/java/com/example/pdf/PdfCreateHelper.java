package com.example.pdf;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.example.pdf.vo.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.example.pdf.PdfUtil.*;


/**
 * @Description 创建pdf的bean
 * @Author c-zhongwh01
 * @Date 2023/6/14 15:03
 */
@Component
@Slf4j
public class PdfCreateHelper {

    private static final Integer ATTACHMENT_SIZE_CB = 10;
    private static final Integer ATTACHMENT_SIZE_SINGLE = 7;
    private Integer alignment = null;

    public static void main(String[] args) {
        String tem =
                "{\"address\":\"广东省深圳市福田区福田街道测试\",\"area\":\"100\",\"customerMobile\":\"19000000002\",\"customerName\":\"啊啊\",\"designer\":\"手机用户6930\",\"productQuotedVos\":[{\"quotedDetailVos\":[{\"amount\":\"0.00\",\"branch\":\"官丹测试7.2\",\"budgetCount\":\"1\",\"code\":\"87\",\"materialDesc\":\"\",\"name\":\"商场询价商品001\",\"price\":\"0.00\",\"region\":\"\"},{\"amount\":\"0.00\",\"branch\":\"官丹测试7.2\",\"budgetCount\":\"1\",\"code\":\"87\",\"materialDesc\":\"\",\"name\":\"商场询价商品001\",\"price\":\"0.00\",\"region\":\"\"},{\"amount\":\"0.00\",\"branch\":\"官丹测试7.2\",\"budgetCount\":\"1\",\"code\":\"136\",\"materialDesc\":\"\",\"name\":\"超级长的商品名称以制作工艺命名以制作工艺命名以制作\",\"price\":\"0.00\",\"region\":\"\"}],\"total\":27,\"type\":\"标配方案\"}],\"productSolutionVo\":{\"solutionDetailVos\":[{\"amount\":\"120\",\"area\":\"100\",\"name\":\"毛毛测试组合\",\"price\":\"10\",\"series\":\"ffff\"}]},\"store\":\"啊发发是\",\"type\":\"施工服务\"}";
        RenovationAttachmentVO renovationAttachmentVO = JSON.parseObject(tem, RenovationAttachmentVO.class);
        PdfCreateHelper pdfCreateHelper = new PdfCreateHelper();
        pdfCreateHelper.createPdfToBase64(renovationAttachmentVO);
    }

    public String createPdfToBase64(RenovationAttachmentVO renovationAttachmentVO) {
        String file;
        List<ProductQuotedDetailVo> quotedDetailVos = renovationAttachmentVO.getQuotedDetailVos();
        try {
            if (RenovationDocNameEnum.CB.getName().equals(renovationAttachmentVO.getType())
                    || (RenovationDocNameEnum.SG.getName().equals(renovationAttachmentVO.getType()) && CollectionUtils.isEmpty(quotedDetailVos))) {
                file = this.createRenovationCbAttachment(renovationAttachmentVO);
            } else {
                file = this.createRenovationAttachment(renovationAttachmentVO);
            }
            return file;
        } catch (RuntimeException e) {
            throw new ExceptionConverter(e);
        }
    }

    private String createRenovationAttachment(RenovationAttachmentVO renovationAttachmentVO) {
        // 1. 新建document对象
        Document document = PdfUtil.getA4DocumentWithBusinessType(renovationAttachmentVO.getType());
        // 2. 文件路径设置
        String pdfPath = PdfUtil.checkPdfPath("/pdf/");
        String file = pdfPath + UUID.randomUUID() + ".pdf";
        // 3. 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
        try (FileOutputStream outputStream = new FileOutputStream(file);) {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            // 4.创建页面事件 - 添加页眉
            PdfEvent event = new PdfEvent();
            writer.setPageEvent(event);
            // 5.打开文档
            document.open();
            // 6. 添加附件内容
            String text = "";
            if (RenovationDocNameEnum.SG.getName().equals(renovationAttachmentVO.getType())) {
                text = AttachmentConstants.SG_ATTACHMENT_ONE;
            } else if (RenovationDocNameEnum.JC.getName().equals(renovationAttachmentVO.getType())) {
                text = AttachmentConstants.JC_ATTACHMENT_ONE;
            }
            this.creatFirstTextWithStyle(document, text, 2f, 60f);
            // 添加7列表格
            PdfPTable table = new PdfPTable(ATTACHMENT_SIZE_SINGLE);
            table.setTotalWidth(new float[]{100, 100, 100, 100, 100, 100, 100});

            Font font = PdfUtil.getFont(null);
            // 6.1. 表格内容
            this.createProductSingleQuoted(table, renovationAttachmentVO, font);

            // 7.文档添加表格
            document.add(table);
            // 8.关闭文档
            document.close();

            return PdfUtil.getBase64(FileUtil.readBytes(file));

        } catch (DocumentException | ExceptionConverter | IOException e) {
            throw new ExceptionConverter(e);
        } finally {
            FileUtil.del(file);
            document.close();
        }
    }

    public String createRenovationCbAttachment(RenovationAttachmentVO renovationAttachmentVO) {

        // 1. 新建document对象
        Document document = PdfUtil.getA4DocumentWithBusinessType(renovationAttachmentVO.getType());

        List<ProductQuotedDetailVo> quotedDetailVos = renovationAttachmentVO.getQuotedDetailVos();
        String flag = CollectionUtils.isEmpty(quotedDetailVos) ? "1" : "0";
        PdfName businessType = new PdfName("flag");
        document.setAccessibleAttribute(businessType, new PdfString(flag));
        // 2. 文件路径设置
        String pdfPath = PdfUtil.checkPdfPath("/pdf/");
        String file = pdfPath + UUID.randomUUID() + ".pdf";

        // 3. 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            setA4HorizontalPage(document);

            // 4.创建页面事件 - 添加页眉
            PdfEvent event = new PdfEvent();
            writer.setPageEvent(event);

            // 5.打开文档
            document.open();

            // 6. 添加附件内容
            this.creatOneText(document);
            // 添加10列表格
            PdfPTable table = new PdfPTable(ATTACHMENT_SIZE_CB);
            table.setTotalWidth(new float[]{100, 200, 100, 100, 100, 100, 100, 100, 100, 100});

            Font font = PdfUtil.getFont(null);
            // 6.1. 设置基本信息
            this.createBaseInfo(table, renovationAttachmentVO, font);
            // 6.2. 设置产品解决方案
            this.createProductSolution(table, renovationAttachmentVO, font);
            // 6.3. 设置产品类型报价明细
            this.createProductQuoted(table, renovationAttachmentVO, font);
            // 6.4. 设置总计金额
            this.createTotalAmount(table, renovationAttachmentVO, font);

            // 7.文档添加表格
            document.add(table);
            // 8.关闭文档
            document.close();

            return getBase64(FileUtil.readBytes(file));

        } catch (DocumentException | ExceptionConverter | IOException e) {
            throw new ExceptionConverter(e);
        } finally {
            FileUtil.del(file);
            document.close();
        }

    }

    private void createTotalAmount(PdfPTable table, RenovationAttachmentVO renovationAttachmentVO, Font font) {
        BigDecimal total = renovationAttachmentVO.getTotalAmount();
        table.addCell(getPDFCellWithMerge(AttachmentConstants.AMOUNT_TOTAL, font, alignment, 5));
        String totalStr = total != null ? String.valueOf(total) : AttachmentConstants.EMPTY;
        table.addCell(getPDFCell(totalStr, font, alignment));
        table.addCell(getPDFCellWithMerge(AttachmentConstants.EMPTY, font, alignment, ATTACHMENT_SIZE_CB));
    }

    private void createProductQuoted(PdfPTable table, RenovationAttachmentVO renovationAttachmentVO, Font font) {
        List<ProductQuotedVo> productQuotedVos = renovationAttachmentVO.getProductQuotedVos();
        productQuotedVos.stream().forEach(productQuotedVo -> {
            String title = productQuotedVo.getType();
            table.addCell(getPDFCellWithMerge(title, font, Element.ALIGN_LEFT, ATTACHMENT_SIZE_CB));
            this.createProductTableHead(table, font);
            productQuotedVo.getQuotedDetailVos().stream().forEach(v -> {
                table.addCell(getPDFCell(v.getCode(), font, alignment));
                table.addCell(getPDFCell(v.getName(), font, alignment));
                table.addCell(getPDFCell(v.getBudgetCount(), font, alignment));
                table.addCell(getPDFCell(v.getUnit(), font, alignment));
                table.addCell(getPDFCell(v.getPrice(), font, alignment));
                table.addCell(getPDFCell(String.valueOf(v.getAmount()), font, alignment));
                table.addCell(getPDFCell(v.getBranch(), font, alignment));
                table.addCell(getPDFCell(v.getRegion(), font, alignment));
                table.addCell(getPDFCellWithMerge(v.getMaterialDesc(), font, alignment, ATTACHMENT_SIZE_CB));
            });
            this.createProductAmountTotal(table, productQuotedVo.getTotal(), font, 5);
        });
    }

    private void createProductTableHead(PdfPTable table, Font font) {
        List<String> productTableHead = AttachmentConstants.PRODUCT_QUOTED_TABLE_HEAD;
        int size = productTableHead.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                table.addCell(getPDFCellWithMerge(productTableHead.get(i), font, alignment, ATTACHMENT_SIZE_CB));
            } else {
                table.addCell(getPDFCell(productTableHead.get(i), font, alignment));
            }
        }
    }

    private void createProductSingleQuoted(PdfPTable table, RenovationAttachmentVO renovationAttachmentVO, Font font) {
        List<String> singleTableHead = AttachmentConstants.PRODUCT_SINGLE_TABLE_HEAD;
        int size = singleTableHead.size();
        for (int i = 0; i < size; i++) {
            if (i == 1) {
                table.addCell(getPDFCellWithMerge(singleTableHead.get(i), font, alignment, 3));
            } else {
                table.addCell(getPDFCell(singleTableHead.get(i), font, alignment));
            }
        }
        List<ProductQuotedDetailVo> quotedDetailVos = renovationAttachmentVO.getQuotedDetailVos();
        int quotedSize = quotedDetailVos.size();
        for (int i = 0; i < quotedSize; i++) {
            table.addCell(getPDFCell(String.valueOf(i + 1), font, alignment));
            table.addCell(getPDFCellWithMerge(quotedDetailVos.get(i).getName(), font, alignment, 3));
            table.addCell(getPDFCell(quotedDetailVos.get(i).getBudgetCount(), font, alignment));
            table.addCell(getPDFCell(quotedDetailVos.get(i).getUnit(), font, alignment));
            table.addCell(getPDFCell(quotedDetailVos.get(i).getAmount(), font, alignment));
        }
        table.addCell(getPDFCell("合计", font, alignment));
        table.addCell(getPDFCellWithMerge("", font, alignment, 3));
        table.addCell(getPDFCell("", font, alignment));
        table.addCell(getPDFCell("", font, alignment));
        table.addCell(getPDFCell(String.valueOf(renovationAttachmentVO.getTotal()), font, alignment));
    }

    private void createProductSolution(PdfPTable table, RenovationAttachmentVO renovationAttachmentVO, Font font) {
        table.addCell(getPDFCellWithMerge(AttachmentConstants.PRODUCT_SOLUTION_TYPE, font, Element.ALIGN_LEFT, ATTACHMENT_SIZE_CB));

        List<String> solutionTableHead = AttachmentConstants.PRODUCT_SOLUTION_TABLE_HEAD;
        int size = solutionTableHead.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                table.addCell(getPDFCellWithMerge(solutionTableHead.get(i), font, alignment, ATTACHMENT_SIZE_CB));
            } else if (AttachmentConstants.CONSTANT_AREA.equals(solutionTableHead.get(i))) {
                table.addCell(getPDFCellSpecial(solutionTableHead.get(i), font, alignment));
            } else {
                table.addCell(getPDFCell(solutionTableHead.get(i), font, alignment));
            }
        }
        ProductSolutionVo productSolutionVo = renovationAttachmentVO.getProductSolutionVo();
        List<ProductSolutionDetailVo> solutionDetailVos = productSolutionVo.getSolutionDetailVos();
        if (CollectionUtils.isNotEmpty(solutionDetailVos)) {
            solutionDetailVos.stream().forEach(v -> {
                table.addCell(getPDFCell(v.getSeries(), font, alignment));
                table.addCell(getPDFCell(v.getName(), font, alignment));
                table.addCell(getPDFCell(v.getArea(), font, alignment));
                table.addCell(getPDFCell(v.getPrice(), font, alignment));
                table.addCell(getPDFCell(v.getAmount(), font, alignment));
                table.addCell(getPDFCellWithMerge(v.getAmountDesc(), font, alignment, ATTACHMENT_SIZE_CB));
            });
        } else {
            table.addCell(getPDFCell(AttachmentConstants.EMPTY, font, alignment));
            table.addCell(getPDFCell(AttachmentConstants.EMPTY, font, alignment));
            table.addCell(getPDFCell(AttachmentConstants.EMPTY, font, alignment));
            table.addCell(getPDFCell(AttachmentConstants.EMPTY, font, alignment));
            table.addCell(getPDFCell(AttachmentConstants.EMPTY, font, alignment));
            table.addCell(getPDFCellWithMerge(AttachmentConstants.EMPTY, font, alignment, ATTACHMENT_SIZE_CB));
        }

        this.createProductAmountTotal(table, productSolutionVo.getTotal(), font, 4);
    }

    private void createProductAmountTotal(PdfPTable table, BigDecimal total, Font font, int colSpan) {
        table.addCell(getPDFCellWithMerge(AttachmentConstants.PRODUCT_AMOUNT_TOTAL, font, alignment, colSpan));
        String totalStr = total != null ? String.valueOf(total) : AttachmentConstants.EMPTY;
        table.addCell(getPDFCell(totalStr, font, alignment));
        table.addCell(getPDFCellWithMerge(AttachmentConstants.EMPTY, font, alignment, ATTACHMENT_SIZE_CB));
    }

    private void createBaseInfo(PdfPTable table, RenovationAttachmentVO renovationAttachmentVO, Font font) {
        table.addCell(getPDFCell(AttachmentConstants.BASE_STORE, font, alignment));
        table.addCell(getPDFCell(renovationAttachmentVO.getStore(), font, alignment));
        table.addCell(getPDFCell(AttachmentConstants.BASE_DESIGNER, font, alignment));
        table.addCell(getPDFCell(renovationAttachmentVO.getDesigner(), font, alignment));
        table.addCell(getPDFCell(AttachmentConstants.BASE_ADDRESS, font, alignment));
        table.addCell(getPDFCellWithMerge(renovationAttachmentVO.getAddress(), font, alignment, ATTACHMENT_SIZE_CB));
        table.addCell(getPDFCell(AttachmentConstants.BASE_CUSTOMER_NAME, font, alignment));
        table.addCell(getPDFCell(renovationAttachmentVO.getCustomerName(), font, alignment));
        table.addCell(getPDFCell(AttachmentConstants.BASE_CUSTOMER_MOBILE, font, alignment));
        table.addCell(getPDFCell(renovationAttachmentVO.getCustomerMobile(), font, alignment));
        table.addCell(getPDFCellSpecial(AttachmentConstants.BASE_AREA, font, alignment));
        table.addCell(getPDFCell(renovationAttachmentVO.getArea(), font, alignment));
        table.addCell(getPDFCell(AttachmentConstants.BASE_HOUSE_TYPE, font, alignment));
        table.addCell(getPDFCellWithMerge(renovationAttachmentVO.getHouseType(), font, alignment, ATTACHMENT_SIZE_CB));

    }

    private void creatOneText(Document document) throws DocumentException {
        Paragraph elements = new Paragraph(AttachmentConstants.CB_ATTACHMENT_ONE, PdfUtil.getBoldFont(null));
        elements.setLeading(3f);
        elements.setSpacingAfter(10f);
        elements.setIndentationLeft(80f);
        document.add(elements);
    }

    private void creatFirstTextWithStyle(Document document, String text, float fixedLeading, float indentationLeft) throws DocumentException {
        Paragraph elements = new Paragraph(text, PdfUtil.getBoldFont(null));
        elements.setLeading(fixedLeading);
        elements.setSpacingAfter(10f);
        elements.setIndentationLeft(indentationLeft);
        document.add(elements);
    }
}
