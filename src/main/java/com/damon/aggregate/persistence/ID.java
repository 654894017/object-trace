package com.damon.aggregate.persistence;

public interface ID<T> {

    T getId();

    void setId(T id);
}
