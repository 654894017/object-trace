package com.damon.object_trace.utils;

import cn.hutool.core.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectUtils extends ReflectUtil {

    public static String getFieldNameByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        // 获取所有字段
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            Annotation anno = field.getAnnotation(annotation);
            // 判断字段是否有指定注解
            if (anno != null) {
                return field.getName();
            }
        }
        return null;
    }
}
