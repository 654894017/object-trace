package com.damon.object_trace.comparator;

import com.damon.object_trace.Version;
import org.apache.commons.lang3.builder.DiffResult;

public class DiffDeepComparator implements DeepComparator {
    @Override
    public <T extends Version> boolean isDeepEquals(T a, T b) {
        DiffResult<T> result = a.diff(b);
        return result.getDiffs().size() == 0;
    }
}
