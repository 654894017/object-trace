package com.damon.object_trace;


public interface Versionable<K> extends ID<K> {
    Integer getVersion();

    void setVersion(Integer version);
}
