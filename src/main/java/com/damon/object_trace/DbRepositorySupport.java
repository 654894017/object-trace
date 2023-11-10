package com.damon.object_trace;

import com.damon.object_trace.comparator.ChangedEntity;
import com.damon.object_trace.comparator.ObjectComparator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public abstract class DbRepositorySupport {

    public static <T, ID> List<T> findNewEntities(List<T> newEntities, List<T> oldEntities, Function<T, ID> getId) {
        Set<ID> newIds = (Set) newEntities.stream().map(getId).collect(Collectors.toSet());
        Set<ID> oldIds = (Set) oldEntities.stream().map(getId).collect(Collectors.toSet());
        newIds.removeAll(oldIds);
        return (List) newEntities.stream().filter((item) -> {
            return newIds.contains(getId.apply(item));
        }).collect(Collectors.toList());
    }

    public static <T> List<T> findNewEntities(List<T> newEntities, Predicate<T> predicate) {
        return (List) newEntities.stream().filter(predicate).collect(Collectors.toList());
    }

    public <A extends Version, B> Boolean executeSafeUpdate(B newObj, B oldObj, Function<B, A> function) {
        A newObject = function.apply(newObj);
        A oldObject = function.apply(oldObj);
        Set<String> changedFields = ObjectComparator.findChangedFields(newObject, oldObject);
        return update(newObject, changedFields);
    }

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
            itemPOS = ObjectComparator.findNewEntities(newItems, oldItems, predicate::test);
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
