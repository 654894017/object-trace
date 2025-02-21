package com.damon.aggregate.persistence.copier;

public interface DeepCopier {
    <T> T copy(T object);
}
