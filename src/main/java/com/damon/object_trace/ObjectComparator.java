package com.damon.object_trace;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ObjectComparator {

    public static Set<String> findChangedFields(Object newObject, Object oldObject) {
        return findChangedFields(newObject, oldObject, true);
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
                            differentProperties.add(toUnderScoreCase(field.getName()));
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
     * 下划线转驼峰命名
     */
    private static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append("_");
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    public static <T, ID> List<T> findNewEntities(List<T> newEntities, List<T> oldEntities, Function<T, ID> getId) {
        Set<ID> newIds = newEntities.stream().map(getId).collect(Collectors.toSet());
        Set<ID> oldIds = oldEntities.stream().map(getId).collect(Collectors.toSet());
        newIds.removeAll(oldIds);
        return newEntities.stream().filter((item) -> newIds.contains(getId.apply(item))).collect(Collectors.toList());
    }

    public static <T> List<T> findNewEntities(List<T> newEntities, Predicate<T> predicate) {
        return newEntities.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, ID> List<T> findRemovedEntities(List<T> newEntities, List<T> oldEntities, Function<T, ID> getId) {
        Set<ID> newIds = newEntities.stream().map(getId).collect(Collectors.toSet());
        Set<ID> oldIds = oldEntities.stream().map(getId).collect(Collectors.toSet());
        oldIds.removeAll(newIds);
        return oldEntities.stream().filter((item) -> oldIds.contains(getId.apply(item))).collect(Collectors.toList());
    }

    public static <T, ID> List<ChangedEntity<T>> findChangedEntities(List<T> newEntities, List<T> oldEntities, Function<T, ID> getId) {
        Map<ID, T> newEntityMap = newEntities.stream().collect(Collectors.toMap(getId, Function.identity()));
        Map<ID, T> oldEntityMap = oldEntities.stream().collect(Collectors.toMap(getId, Function.identity()));
        oldEntityMap.keySet().retainAll(newEntityMap.keySet());
        List<ChangedEntity<T>> results = new ArrayList();
        Iterator iterator = oldEntityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ID, T> entry = (Map.Entry) iterator.next();
            T oldEntity = entry.getValue();
            T newEntity = newEntityMap.get(entry.getKey());
            Set<String> list = findChangedFields(newEntity, oldEntity);
            if (!list.isEmpty()) {
                results.add(new ChangedEntity(oldEntity, newEntity));
            }
        }
        return results;
    }
}
