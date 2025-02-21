package com.damon.aggregate.persistence;


public interface Versionable<K> extends ID<K> {
    Integer getVersion();

    /**
     * 新的实体默认填0或者不填写  Aggregate#NEW_VERSION
     *
     * @param version
     * @see Aggregate#isNew()
     */
    void setVersion(Integer version);
}
