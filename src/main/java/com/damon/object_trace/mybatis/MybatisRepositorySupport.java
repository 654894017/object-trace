package com.damon.object_trace.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.damon.object_trace.DbRepositorySupport;
import com.damon.object_trace.ID;
import com.damon.object_trace.Version;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MybatisRepositorySupport extends DbRepositorySupport implements ApplicationContextAware {
    private Map<String, BaseMapper> baseMapperMap;
    private BaseMapper getBaseMapper(String typeName) {
        return baseMapperMap.get(typeName);
    }
    @Override
    protected <A extends ID> void deleteBatch(List<A> removedItem) {
        Set<Long> removedItemIds = removedItem.stream().map(A::getId).collect(Collectors.toSet());
        getBaseMapper(getTypeName(removedItem.get(0))).deleteBatchIds(removedItemIds);
    }

    @Override
    protected <A extends ID> void insert(A entity) {
        getBaseMapper(getTypeName(entity)).insert(entity);
    }

    @Override
    protected <A extends ID> Boolean update(A entity, Set<String> changedFields) {
        UpdateWrapperNew<A> wrapper = new UpdateWrapperNew<>();
        wrapper.set(changedFields, entity);
        wrapper.eq("id", entity.getId());
        int result = getBaseMapper(getTypeName(entity)).update(null, wrapper);
        return result > 1;
    }

    @Override
    protected <A extends Version> Boolean update(A entity, Set<String> changedFields) {
        UpdateWrapperNew<A> wrapper = new UpdateWrapperNew<>();
        wrapper.set(changedFields, entity);
        wrapper.set("version", entity.getVersion() + 1);
        wrapper.eq("version", entity.getVersion());
        wrapper.eq("id", entity.getId());
        int result = getBaseMapper(getTypeName(entity)).update(null, wrapper);
        return result > 1;
    }

    private String getTypeName(Object entity) {
        return entity.getClass().getTypeName();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Map<String, BaseMapper> map = applicationContext.getBeansOfType(BaseMapper.class);
        this.baseMapperMap = new HashMap<>(map.size());
        map.values().forEach(baseMapper -> {
            Class<?>[] interfaces = baseMapper.getClass().getInterfaces();
            for (Class<?> classes : interfaces) {
                Type[] baseMapperGenericInterfaces = classes.getGenericInterfaces();
                for (Type type : baseMapperGenericInterfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        for (Type typeArgument : typeArguments) {
                            if (typeArgument instanceof Class) {
                                Class<?> typeClass = (Class<?>) typeArgument;
                                baseMapperMap.put(typeClass.getTypeName(), baseMapper);
                            }
                        }
                    }
                }
            }
        });
    }

}
