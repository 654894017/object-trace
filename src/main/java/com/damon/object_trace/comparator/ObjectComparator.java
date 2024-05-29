package com.damon.object_trace.comparator;

import cn.hutool.core.util.StrUtil;
import com.damon.object_trace.ID;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
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
        if (newObject != null && oldObject != null && newObject.getClass().equals(oldObject.getClass())) {
            Class<?> clazz = newObject.getClass();
            Field[] fields = FieldUtils.getAllFields(clazz);
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object newValue = field.get(newObject);
                    Object oldeValue = field.get(oldObject);
                    if (ObjectUtils.notEqual(newValue, oldeValue)) {
                        if (toUnderlineCase) {
                            differentProperties.add(StrUtil.toUnderlineCase(field.getName()));
                        } else {
                            differentProperties.add(field.getName());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(field.getName(), e);
                }
            }
        }
        return differentProperties;
    }

    /**
     * 查询列表新增的实体(默认ID为空或新列表中的ID在旧的列表中不存在都当新的实体处理)
     *
     *
     *
     * @param newEntities
     * @param oldEntities
     * @return
     * @param <T>
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
