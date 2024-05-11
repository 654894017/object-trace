package com.damon.object_trace;

import com.damon.object_trace.comparator.ChangedEntity;
import com.damon.object_trace.comparator.ObjectComparator;
import com.damon.object_trace.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
     *
     * @param newItem
     * @param oldItem
     * @param convert
     * @param <A>
     * @param <B>
     * @return
     */
    public <A extends ID, B> Boolean executeUpdateList(B newItem, B oldItem, Function<B, List<A>> convert) {
        return executeUpdateList(newItem, oldItem, convert, null);
    }

    /**
     * 列表模式的增量更新(自动处理新增、修改、删除的实体)
     *
     * @param newItem
     * @param oldItem
     * @param convert
     * @param predicate
     * @param <A>
     * @param <B>
     * @return
     */
    public <A extends ID, B> Boolean executeUpdateList(B newItem, B oldItem, Function<B, List<A>> convert, Predicate<A> predicate) {
        List<A> newItems = convert.apply(newItem);
        List<A> oldItems = convert.apply(oldItem);
        List<A> itemPOS;
        if (predicate == null) {
            itemPOS = ObjectComparator.findNewEntities(newItems, oldItems, A::getId);
        } else {
            itemPOS = ObjectComparator.findNewEntities(newItems, predicate::test);
        }
        for (A item : itemPOS) {
            Boolean result = insert(item);
            if (!result) {
                log.warn("create item failed, type: {} , info : {}", item.getClass().getTypeName(), JsonUtils.jsonToString(item));
                return Boolean.FALSE;
            }
        }

        List<ChangedEntity<A>> changedEntityList = ObjectComparator.findChangedEntities(newItems, oldItems, A::getId);
        for (ChangedEntity<A> changedEntity : changedEntityList) {
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

        List<A> removedItems = ObjectComparator.findRemovedEntities(newItems, oldItems, A::getId);
        if (!removedItems.isEmpty()) {
            Boolean result = deleteBatch(removedItems);
            if (!result) {
                Set<Object> removedItemIds = removedItems.stream().map(A::getId).collect(Collectors.toSet());
                log.warn("delete item failed, type: {} , ids : {}", removedItems.get(0).getClass().getTypeName(), removedItemIds);
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    protected abstract <A extends ID> Boolean deleteBatch(List<A> removedItems);

    protected abstract <A extends ID> Boolean insert(A entity);

    protected abstract <A extends ID> Boolean update(A entity, Set<String> changedFields);

    protected abstract <A extends Versionable> Boolean update(A entity, Set<String> changedFields);


}
