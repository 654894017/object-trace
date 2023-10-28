package com.damon.object_trace;

import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;
import org.apache.commons.lang3.builder.ReflectionDiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public interface Version<T> extends Diffable<T> {
    int getVersion();

    default DiffResult<T> diff(T obj) {
        return new ReflectionDiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE).build();
    }
}
