package com.example.log.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
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
     * 关键参数参数
     */
    private Object param;

    /**
     * 不重复添加判断key，不为空则模块-功能-key判重
     */
    private String repeatKey;

    /**
     * 是否落库，默认true;如果false，则仅仅日志打印
     */
    private boolean saveDb = true;

    public KeyLogBo(String module, String func) {
        this.module = module;
        this.func = func;
    }

    public KeyLogBo(boolean saveDb) {
        this.saveDb = saveDb;
    }
}
