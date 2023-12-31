package com.damon.object_trace;

import com.damon.object_trace.comparator.DeepComparator;
import com.damon.object_trace.comparator.DiffDeepComparator;
import com.damon.object_trace.copier.DeepCopier;
import com.damon.object_trace.copier.JsonDeepCopier;

public class AggregateFactory {
    private static DeepCopier deepCopier = new JsonDeepCopier();
    private static DeepComparator deepComparator = new DiffDeepComparator();

    private AggregateFactory() {
        throw new IllegalStateException("A factory class, please use static method");
    }

    public static <R extends Version> Aggregate<R> createAggregate(R root) {
        return new Aggregate(root, deepCopier, deepComparator);
    }

    public static <R extends Version> Aggregate<R> createAggregate(R root, DeepCopier deepCopier, DeepComparator deepComparator) {
        return new Aggregate(root, deepCopier, deepComparator);
    }

}
