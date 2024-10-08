package com.damon.object_trace;

import com.damon.object_trace.copier.DeepCopier;
import com.damon.object_trace.copier.JsonDeepCopier;

public class AggregateFactory {
    private static DeepCopier deepCopier = new JsonDeepCopier();

    private AggregateFactory() {
        throw new IllegalStateException("A factory class, please use static method");
    }

    public static <R> Aggregate<R> createAggregate(R root) {
        return new Aggregate(root, deepCopier);
    }

    public static <R> Aggregate<R> createAggregate(R root, DeepCopier deepCopier) {
        return new Aggregate(root, deepCopier);
    }

}
