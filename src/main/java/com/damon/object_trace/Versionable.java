package com.damon.object_trace;

public interface Versionable<T, K> extends ID<K> {
    Integer getVersion();

    void setVersion(Integer version);
//    @Override
//    default DiffResult<T> diff(T obj) {
//        return new ReflectionDiffBuilder(this, obj, ToStringStyle.SHORT_PREFIX_STYLE).build();
//    }
}
