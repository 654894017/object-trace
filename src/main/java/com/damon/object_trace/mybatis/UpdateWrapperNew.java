package com.damon.object_trace.mybatis;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.Set;

public class UpdateWrapperNew<T> extends UpdateWrapper<T> {

    public void set(Set<String> changedFields, T t) {
        for (String field : changedFields) {
            set(StrUtil.toUnderlineCase(field), ReflectUtil.getFieldValue(t, field));
        }
    }

}
