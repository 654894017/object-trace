package com.damon.object_trace;


import com.damon.object_trace.comparator.DeepComparator;
import com.damon.object_trace.copier.DeepCopier;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class Aggregate<R extends Versionable> {
    private final int NEW_VERSION = 0;
    private R root;
    private R snapshot;
    private DeepComparator deepComparator;

    public Aggregate(R root, DeepCopier deepCopier, DeepComparator deepComparator) {
        if (root == null) {
            return;
        }
        this.root = root;
        this.snapshot = deepCopier.copy(root);
        this.deepComparator = deepComparator;
    }

    /**
     * Whether the aggregate is changed.
     *
     * @return true if the aggregate is changed, false if the aggregate is unchanged.
     */
    public boolean isChanged() {
//        Boolean result= Objects.deepEquals(root, snapshot);
///        return !deepComparator.isDeepEquals(root, snapshot);
        return !EqualsBuilder.reflectionEquals(root, snapshot, false);
    }

//    public boolean isNew() {
//        return root.getVersion() == NEW_VERSION;
//    }

    public R getRoot() {
        return root;
    }

    public R getSnapshot() {
        return snapshot;
    }
}


