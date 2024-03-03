package com.example.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

/**
 * 注意事项： 填充原理是直接给entity的属性设置值!!! 注解则是指定该属性在对应情况下必有值,如果无值则入库会是null
 * MetaObjectHandler提供的默认方法的策略均为:如果属性有值则不覆盖,如果填充值为null则不填充 字段必须声明TableField注解,属性fill选择对应策略,该声明告知Mybatis-Plus需要预留注入SQL字段
 * 填充处理器MyMetaObjectHandler在 Spring Boot 中需要声明@Component或@Bean注入
 * 要想根据注解FieldFill.xxx和字段名以及字段类型来区分必须使用父类的strictInsertFill或者strictUpdateFill方法 不需要根据任何来区分可以使用父类的fillStrategy方法
 */
@Component
public class CustomerMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATED_BY = "createdBy";
    private static final String UPDATED_BY = "updatedBy";
    private static final String CREATED_TIME = "createdTime";
    private static final String UPDATED_TIME = "updatedTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        this.insertCreatedByOrUpdatedBy(metaObject, CREATED_BY, UPDATED_BY);
        if (metaObject.hasSetter(CREATED_TIME) || metaObject.hasSetter(UPDATED_TIME)) {
            setFiledNull(metaObject, new String[]{CREATED_TIME, UPDATED_TIME});
            this.strictInsertFill(metaObject, CREATED_TIME, Date.class, new Date());
            this.strictInsertFill(metaObject, UPDATED_TIME, Date.class, new Date());
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATED_BY)) {
            // 一般在上下文中获取
            String userId = "admin";
            setFiledNull(metaObject, new String[]{UPDATED_BY});
            this.strictUpdateFill(metaObject, UPDATED_BY, String.class, userId);
        }

        if (metaObject.hasSetter(UPDATED_TIME)) {
            setFiledNull(metaObject, new String[]{UPDATED_TIME});
            this.strictUpdateFill(metaObject, UPDATED_TIME, Date.class, new Date());
        }
    }

    private void insertCreatedByOrUpdatedBy(MetaObject metaObject, String createdByColumnName,
                                            String updatedByColumnName) {
        if (metaObject.hasGetter(createdByColumnName) || metaObject.hasSetter(updatedByColumnName)) {
            // 一般在上下文中获取
            String userId = "admin";
            setFiledNull(metaObject, new String[]{createdByColumnName, updatedByColumnName});
            this.strictInsertFill(metaObject, createdByColumnName, String.class, userId);
            this.strictInsertFill(metaObject, updatedByColumnName, String.class, userId);
        }
    }

    private void setFiledNull(MetaObject metaObject, String[] tableFields) {
        Arrays.stream(tableFields).forEach(e -> {
            if (metaObject.hasSetter(e)) {
                metaObject.setValue(e, null);
            }
        });
    }
}
