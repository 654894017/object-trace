package com.damon.aggregate.persistence.comparator;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.damon.aggregate.persistence.ID;
import com.damon.aggregate.persistence.exception.AggregatePersistenceException;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ObjectComparator {

    public static Set<String> findChangedFields(Object newObject, Object oldObject) {
        return findChangedFields(newObject, oldObject, false);
    }

    public static Set<String> findChangedFields(Object newObject, Object oldObject, boolean toUnderlineCase) {
        Set<String> differentProperties = new HashSet<>();
        if (isValidComparison(newObject, oldObject)) {
            Class<?> clazz = newObject.getClass();
            Field[] fields = ReflectUtil.getFields(clazz);
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object newValue = field.get(newObject);
                    Object oldValue = field.get(oldObject);
                    if (!isEquals(newValue, oldValue)) {
                        if (toUnderlineCase) {
                            differentProperties.add(StrUtil.toUnderlineCase(field.getName()));
                        } else {
                            differentProperties.add(field.getName());
                        }
                    }
                } catch (Exception e) {
                    throw new AggregatePersistenceException(field.getName(), e);
                }
            }
        }
        return differentProperties;
    }

    private static boolean isValidComparison(Object obj1, Object obj2) {
        return obj1 != null && obj2 != null && obj1.getClass().equals(obj2.getClass());
    }

    /**
     * 对象为字符串时:  null == ''
     *
     * @param newValue
     * @param oldValue
     * @return
     */
    private static boolean isEquals(Object newValue, Object oldValue) {
        if (newValue == null && StrUtil.EMPTY.equals(oldValue)) {
            return true;
        }
        return ObjectUtil.equal(newValue, oldValue);
    }

    /**
     * 查询列表新增的实体(默认ID为空或新列表中的ID在旧的列表中不存在都当新的实体处理)
     *
     * @param newEntities
     * @param oldEntities
     * @param <T>
     * @return
     */
    public static <T extends ID> Collection<T> findNewEntities(Collection<T> newEntities, Collection<T> oldEntities) {
        Set<Object> newIds = newEntities.stream().map(T::getId).collect(Collectors.toSet());
        Set<Object> oldIds = oldEntities.stream().map(T::getId).collect(Collectors.toSet());
        newIds.removeAll(oldIds);
        return newEntities.stream().filter((item) -> newIds.contains(item.getId()) || item.getId() == null).collect(Collectors.toList());
    }

    public static <T extends ID> Collection<T> findNewEntities(Collection<T> newEntities, Predicate<T> predicate) {
        return newEntities.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T extends ID> Collection<T> findRemovedEntities(Collection<T> newEntities, Collection<T> oldEntities) {
        Set<Object> newIds = newEntities.stream().map(T::getId).collect(Collectors.toSet());
        Set<Object> oldIds = oldEntities.stream().map(T::getId).collect(Collectors.toSet());
        oldIds.removeAll(newIds);
        return oldEntities.stream().filter((item) -> oldIds.contains(item.getId())).collect(Collectors.toList());
    }

    public static <T extends ID> Collection<ChangedEntity<T>> findChangedEntities(Collection<T> newEntities, Collection<T> oldEntities) {
        Map<Object, T> newEntityMap = newEntities.stream().collect(Collectors.toMap(ID::getId, Function.identity()));
        Map<Object, T> oldEntityMap = oldEntities.stream().collect(Collectors.toMap(ID::getId, Function.identity()));
        //交集
        oldEntityMap.keySet().retainAll(newEntityMap.keySet());
        List<ChangedEntity<T>> results = new ArrayList();
        Iterator iterator = oldEntityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ID, T> entry = (Map.Entry) iterator.next();
            T oldEntity = entry.getValue();
            T newEntity = newEntityMap.get(entry.getKey());
            if (!EqualsBuilder.reflectionEquals(oldEntity, newEntity, false)) {
                results.add(new ChangedEntity(oldEntity, newEntity));
            }
        }
        return results;
    }


}
