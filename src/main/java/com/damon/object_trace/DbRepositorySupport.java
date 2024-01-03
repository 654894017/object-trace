package com.damon.object_trace;

import com.damon.object_trace.comparator.ChangedEntity;
import com.damon.object_trace.comparator.ObjectComparator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;


public abstract class DbRepositorySupport {
    /**
     * 通过数据库乐观锁实现安全更新
     *
     * @param newObj
     * @param oldObj
     * @param function
     * @param <A>
     * @param <B>
     * @return
     */
    public <A extends Version, B> Boolean executeSafeUpdate(B newObj, B oldObj, Function<B, A> function) {
        A newObject = function.apply(newObj);
        A oldObject = function.apply(oldObj);
        Set<String> changedFields = ObjectComparator.findChangedFields(newObject, oldObject);
        return update(newObject, changedFields);
    }

    /**
     * 不带数据库乐观锁的更新，适用非表数据更新
     *
     * @param newObj
     * @param oldObj
     * @param convert
     * @param <A>
     * @param <B>
     * @return
     */
    public <A extends ID, B> Boolean executeUpdate(B newObj, B oldObj, Function<B, A> convert) {
        A newObject = convert.apply(newObj);
        A oldObject = convert.apply(oldObj);
        Set<String> changedFields = ObjectComparator.findChangedFields(newObject, oldObject);
        return update(newObject, changedFields);
    }

    public <A extends ID, B> void executeUpdateList(B newItem, B oldItem, Function<B, List<A>> convert) {
        executeUpdateList(newItem, oldItem, convert, null);
    }

    public <A extends ID, B> void executeUpdateList(B newItem, B oldItem, Function<B, List<A>> convert, Predicate<A> predicate) {
        List<A> newItems = convert.apply(newItem);
        List<A> oldItems = convert.apply(oldItem);
        List<A> itemPOS;
        if (predicate == null) {
            itemPOS = ObjectComparator.findNewEntities(newItems, oldItems, A::getId);
        } else {
            itemPOS = ObjectComparator.findNewEntities(newItems, predicate::test);
        }
        for (A item : itemPOS) {
            insert(item);
        }

        List<ChangedEntity<A>> changedEntityList = ObjectComparator.findChangedEntities(newItems, oldItems, A::getId);
        for (ChangedEntity<A> changedEntity : changedEntityList) {
            Set<String> changedFields = ObjectComparator.findChangedFields(changedEntity.getNewEntity(), changedEntity.getOldEntity());
            if (!changedFields.isEmpty()) {
                update(changedEntity.getNewEntity(), changedFields);
            }
        }

        List<A> removedItems = ObjectComparator.findRemovedEntities(newItems, oldItems, A::getId);
        if (!removedItems.isEmpty()) {
            deleteBatch(removedItems);
        }
    }

    protected abstract <A extends ID> void deleteBatch(List<A> removedItems);

    protected abstract <A extends ID> void insert(A entity);

    protected abstract <A extends ID> Boolean update(A entity, Set<String> changedFields);

    protected abstract <A extends Version> Boolean update(A entity, Set<String> changedFields);


}
