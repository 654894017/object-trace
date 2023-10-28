package com.damon.object_trace;

import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.Diffable;

public class DiffDeepComparator implements DeepComparator {
    @Override
    public <T extends Version> boolean isDeepEquals(T a, T b) {
        DiffResult<T> result = a.diff(b);
        return result.getDiffs().size() == 0;
    }
}
