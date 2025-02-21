package com.damon.aggregate.persistence.comparator;


public class ChangedEntity<T> {
    private final T oldEntity;
    private final T newEntity;

    public ChangedEntity(T oldEntity, T newEntity) {
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    public T getOldEntity() {
        return this.oldEntity;
    }

    public T getNewEntity() {
        return this.newEntity;
    }
}
