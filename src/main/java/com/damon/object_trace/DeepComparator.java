package com.damon.object_trace;

public interface DeepComparator {
    <T extends Version> boolean isDeepEquals(T a, T b);
}
