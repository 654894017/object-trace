package com.damon.object_trace.comparator;

import com.damon.object_trace.Version;

public interface DeepComparator {
    <T extends Version> boolean isDeepEquals(T a, T b);
}
