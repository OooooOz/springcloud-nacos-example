package com.example.pdf.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RenovationDocNameEnum {
    CB("工程承包"), JC("材料销售"), SG("施工服务");

    private String name;

}
