package com.damon.object_trace;

import org.apache.commons.lang3.builder.Diffable;

public interface DeepComparator {
    <T extends Version> boolean isDeepEquals(T a, T b);
}
