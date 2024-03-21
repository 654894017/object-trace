package com.damon.order.infra.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

public class EntityAttributeMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Long.class, System.currentTimeMillis());
        this.strictInsertFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
    }

    @Override
    public MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return MetaObjectHandler.super.setFieldValByName(fieldName, fieldVal, metaObject);
    }


}