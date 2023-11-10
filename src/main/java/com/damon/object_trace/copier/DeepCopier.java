package com.damon.object_trace.copier;

public interface DeepCopier {
    <T> T copy(T object);
}
