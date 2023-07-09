package com.example.pdf;

import com.example.pdf.vo.RenovationDocNameEnum;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * 内部类 添加页眉、页脚
 */
public class PdfEvent extends PdfPageEventHelper {

    // 一页加载完成触发，写入页眉和页脚
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable head = new PdfPTable(1);
        PdfObject businessType = document.getAccessibleAttribute(new PdfName("businessType"));
        PdfObject flag = document.getAccessibleAttribute(new PdfName("flag"));
        try {
            if (RenovationDocNameEnum.CB.getName().equals(businessType.toString())
                    || (RenovationDocNameEnum.SG.getName().equals(businessType.toString()) && flag != null && "1".equals(flag.toString()))) {
                setCbPageHead(head, writer, document);
            } else if (RenovationDocNameEnum.SG.getName().equals(businessType.toString()) || RenovationDocNameEnum.JC.getName().equals(businessType.toString())) {
                setSinglePageHead(head, writer, document);
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    /**
     * Chunk的offsetX控制页眉图片水平位置，offsetY会影响到图片大小； 图片上下位置可通过showTextAligned的y来控制
     * 页眉下划线通过setTotalWidth控制先长度，writeSelectedRows的xPos控制线的起始水平位置
     *
     * @param head
     * @param writer
     * @param document
     * @throws DocumentException
     */
    private void setSinglePageHead(PdfPTable head, PdfWriter writer, Document document) throws DocumentException {
        Font font = PdfUtil.getFont(null);
        // 通过表格构建页眉下划线
        head.setTotalWidth(PageSize.A4.getWidth() - 105);
        head.setWidths(new int[]{24});
        head.setLockedWidth(true);
        head.getDefaultCell().setFixedHeight(-10);
        head.getDefaultCell().setBorder(Rectangle.BOTTOM);
        head.getDefaultCell().setBorderWidth(0.5f);
        head.addCell(new Paragraph(" ", font));
        // 将页眉写到document中，位置可以指定，指定到下面就是页脚
        head.writeSelectedRows(0, -1, 55, PageSize.A4.getHeight() - 20, writer.getDirectContent());
        PdfContentByte directContent = writer.getDirectContent();
        // 最重要的是这个，如果页眉需要设置图片的话，需要在Phrase对象中添加一个Chunk对象，在Chunk对象中添加图片信息即可
        Phrase phrase = new Phrase("", font);
        Image img = PdfUtil.getImg();
        if (img != null) {
            phrase.add(new Chunk(img, 30, -150));
        }
        // 写入页眉
        ColumnText.showTextAligned(directContent, Element.ALIGN_RIGHT, phrase, document.right(), PageSize.A4.getHeight() + 48, 0);
    }

    private void setCbPageHead(PdfPTable head, PdfWriter writer, Document document) throws DocumentException {
        Font font = PdfUtil.getFont(null);
        // 通过表格构建页眉下划线
        head.setTotalWidth(PageSize.A4.getHeight() - 160);
        head.setWidths(new int[]{24});
        head.setLockedWidth(true);
        head.getDefaultCell().setFixedHeight(-10);
        head.getDefaultCell().setBorder(Rectangle.BOTTOM);
        head.getDefaultCell().setBorderWidth(0.5f);
        head.addCell(new Paragraph(" ", font));
        // 将页眉写到document中，位置可以指定，指定到下面就是页脚
        head.writeSelectedRows(0, -1, 80, PageSize.A4.getWidth() - 20, writer.getDirectContent());
        PdfContentByte directContent = writer.getDirectContent();
        // 最重要的是这个，如果页眉需要设置图片的话，需要在Phrase对象中添加一个Chunk对象，在Chunk对象中添加图片信息即可
        Phrase phrase = new Phrase("", font);
        Image img = PdfUtil.getImg();
        if (img != null) {
            phrase.add(new Chunk(img, 0, -150));
        }

        // 写入页眉
        ColumnText.showTextAligned(directContent, Element.ALIGN_RIGHT, phrase, document.right(), PageSize.A4.getWidth() + 48, 0);
    }
}
