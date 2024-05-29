package com.damon.object_trace;


import com.damon.object_trace.comparator.ChangedEntity;
import com.damon.object_trace.comparator.ObjectComparator;
import com.damon.object_trace.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public abstract class DbRepositorySupport {
    private final Logger log = LoggerFactory.getLogger(DbRepositorySupport.class);

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
    public <A extends Versionable, B> Boolean executeSafeUpdate(B newObj, B oldObj, Function<B, A> function) {
        A newObject = function.apply(newObj);
        A oldObject = function.apply(oldObj);
        Set<String> changedFields = ObjectComparator.findChangedFields(newObject, oldObject, false);
        return update(newObject, changedFields);
    }

    /**
     * 不带数据库乐观锁的更新，适用非主表数据更新
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

    /**
     * 列表模式的增量更新(自动处理新增、修改、删除的实体)
     * <br>
     * 注意: 如果id在数据库里不存在则进行新增操作
     *
     * @param newItem
     * @param oldItem
     * @param convert
     * @param <A>
     * @param <B>
     * @return
     */
    public <A extends ID, B> Boolean executeUpdateList(B newItem, B oldItem, Function<B, Collection<A>> convert) {
        return executeUpdateList(newItem, oldItem, convert, null);
    }


    /**
     * 列表模式的增量更新(自动处理新增、修改、删除的实体)
     *
     * @param newItem
     * @param oldItem
     * @param convert
     * @param isNew
     * @param <T>
     * @param <B>
     * @return
     */
    public <T extends ID, B> Boolean executeUpdateList(B newItem,
                                                       B oldItem,
                                                       Function<B, Collection<T>> convert,
                                                       Predicate<T> isNew
    ) {
        Collection<T> newItems = convert.apply(newItem);
        Collection<T> oldItems = convert.apply(oldItem);
        Collection<T> itemPOS;
        if (isNew == null) {
            itemPOS = ObjectComparator.findNewEntities(newItems, oldItems);
        } else {
            itemPOS = ObjectComparator.findNewEntities(newItems, isNew::test);
        }
        for (T item : itemPOS) {
            Boolean result = insert(item);
            if (!result) {
                log.warn("create item failed, type: {} , info : {}", item.getClass().getTypeName(), JsonUtils.jsonToString(item));
                return Boolean.FALSE;
            }
        }

        Collection<ChangedEntity<T>> changedEntityList = ObjectComparator.findChangedEntities(newItems, oldItems);
        for (ChangedEntity<T> changedEntity : changedEntityList) {
            Set<String> changedFields = ObjectComparator.findChangedFields(changedEntity.getNewEntity(), changedEntity.getOldEntity());
            if (!changedFields.isEmpty()) {
                Boolean result = update(changedEntity.getNewEntity(), changedFields);
                if (!result) {
                    log.warn("update item failed, type: {} , info : {} ,change fields : {}",
                            changedEntity.getNewEntity().getClass().getTypeName(), JsonUtils.jsonToString(changedEntity.getNewEntity()), changedFields
                    );
                    return Boolean.FALSE;
                }
            }
        }

        Collection<T> removedItems = ObjectComparator.findRemovedEntities(newItems, oldItems);
        if (removedItems.isEmpty()) {
            return Boolean.TRUE;
        }

        for (T item : removedItems) {
            Boolean result = this.delete(item);
            if (!result) {
                Set<Object> removedItemIds = removedItems.stream().map(T::getId).collect(Collectors.toSet());
                log.warn("delete item failed, type: {} , ids : {}", item.getClass().getTypeName(), removedItemIds);
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    protected abstract <A extends ID> Boolean delete(A item);

    protected abstract <A extends ID> Boolean insert(A entity);

    protected abstract <A extends ID> Boolean update(A entity, Set<String> changedFields);

    protected abstract <A extends Versionable> Boolean update(A entity, Set<String> changedFields);


}
