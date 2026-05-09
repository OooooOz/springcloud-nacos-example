package com.example.importing.controller;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.commons.exporting.infrastructure.handle.SelectedSheetWriteHandler;
import org.commons.exporting.infrastructure.util.ExcelSelectedResolveUtil;
import org.commons.importing.Importer;
import org.commons.importing.Importing;
import org.commons.importing.configure.AbstractCommonDataListener;
import org.commons.importing.model.ImportResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.importing.model.vo.RequirementTemplateExportVo;
import com.example.model.BaseResponse;
import com.example.model.BusinessException;
import com.example.util.EasyExcelUtils;

import cn.hutool.core.io.file.FileNameUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件模板导出
 */
@Slf4j
@RestController
@RequestMapping("/template")
public class EasyExcelController {

    @Autowired
    private Importing importing;
    /**
     * 导出模板
     * @param response
     */
    @GetMapping("/export")
    public void testSimpleExport(HttpServletResponse response) {
        try {
            EasyExcelUtils.initResponseHeader("巡检模板.xlsx", response);
            // 需要引入commons-importing-spring-boot-starter（另一个项目打的包）；没有的话可以手动引用jar
            SelectedSheetWriteHandler handler =
                    new SelectedSheetWriteHandler(ExcelSelectedResolveUtil.resolveSelectedAnnotation(RequirementTemplateExportVo.class));
            EasyExcelFactory
                    .write(response.getOutputStream(), RequirementTemplateExportVo.class)
                    .registerWriteHandler(handler)
                    .sheet("巡检模板")
                    .doWrite(Collections.emptyList());
        } catch (Exception e) {
            throw BusinessException.failMsg("下载模板失败：" + e.getMessage());
        }
    }

    /**
     * 导入模板
     */
    @PostMapping("/import")
    public BaseResponse<ImportResultVO> testImport(@RequestParam("file") MultipartFile file) {
        if (Arrays.stream(ExcelTypeEnum.values()).noneMatch(e -> e.toString().equalsIgnoreCase(FileNameUtil.getSuffix(file.getOriginalFilename())))) {
            throw BusinessException.failMsg("请上传excel类型的模板");
        }

        Importer<RequirementTemplateExportVo> importer = importing.getImporter(RequirementTemplateExportVo.class);
        importer.file(EasyExcelUtils.getInputStreamByFile(file));
        importer.startImport(new AbstractCommonDataListener() {
            @Override
            protected void saveData() {
                log.info("[testImport#saveData]");
            }

//            @Override
//            protected ImportResultVO batchCheckData(List list) {
//                log.info("[testImport#batchCheckData]");
//                ImportResultVO resultVO = new ImportResultVO();
//                if (CollectionUtils.isEmpty(list)) {
//                    return resultVO;
//                }
//                for (Object data : list) {
//                    if (data instanceof RequirementTemplateExportVo) {
//                        RequirementTemplateExportVo vo = (RequirementTemplateExportVo) data;
//                        if (vo.getInspectionUser().contains("A")) {
//                            resultVO.addFailure("批量校验数据异常");
//                        }
//                    }
//                }
//                return resultVO;
//            }

            @Override
            protected void singleCheckData (Object data) {
                log.info("[testImport#checkData]");
                if (data instanceof RequirementTemplateExportVo) {
                    RequirementTemplateExportVo vo = (RequirementTemplateExportVo) data;
                    if (vo.getInspectionUser().contains("A")) {
                        throw BusinessException.failMsg("测试校验异常");
                    }
                }
            }
        });

        ImportResultVO importResultVO = importer.getImportResultVO();
        if (importResultVO.getFailure().get() > 0) {
           return BaseResponse.FAILURE(importResultVO);
        }
        return BaseResponse.SUCCESS(importResultVO);
    }
}
