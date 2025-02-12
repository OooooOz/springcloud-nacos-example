package com.example.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyLogBo {

    /**
     * 模块
     */
    private String module;

    /**
     * 功能
     */
    private String func;

    /**
     * 是否落库，默认true;如果false，则仅仅日志打印
     */
    private boolean saveDb = true;

    public KeyLogBo(String module, String func) {
        this.module = module;
        this.func = func;
    }
}
