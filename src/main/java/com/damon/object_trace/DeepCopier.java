package com.damon.object_trace;

public interface DeepCopier {
    <T> T copy(T object);
}
