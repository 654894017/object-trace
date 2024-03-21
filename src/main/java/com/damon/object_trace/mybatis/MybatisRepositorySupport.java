package com.damon.object_trace.mybatis;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.damon.object_trace.DbRepositorySupport;
import com.damon.object_trace.ID;
import com.damon.object_trace.Versionable;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MybatisRepositorySupport extends DbRepositorySupport {
    @Override
    protected <A extends ID> Boolean deleteBatch(List<A> removedItem) {
        Set<Object> removedItemIds = removedItem.stream().map(A::getId).collect(Collectors.toSet());
        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
        map.put(Constants.COLL, removedItemIds);
        Class<?> clazz = removedItem.get(0).getClass();
        SqlSession sqlSession = getSqlSession(clazz);
        try {
            return SqlHelper.retBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE_BATCH_BY_IDS.getMethod(), clazz), map));
        } finally {
            closeSqlSession(sqlSession, clazz);
        }
    }

    @Override
    protected <A extends ID> Boolean insert(A entity) {
        SqlSession sqlSession = getSqlSession(entity.getClass());
        try {
            return SqlHelper.retBool(sqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE.getMethod(), entity.getClass()), entity));
        } finally {
            closeSqlSession(sqlSession, entity.getClass());
        }
    }

    private String sqlStatement(String sqlMethod, Class<?> entityClass) {
        return SqlHelper.table(entityClass).getSqlStatement(sqlMethod);
    }

    private void closeSqlSession(SqlSession sqlSession, Class<?> entityClass) {
        SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(entityClass));
    }

    private SqlSession getSqlSession(Class<?> entityClass) {
        return SqlSessionUtils.getSqlSession(GlobalConfigUtils.currentSessionFactory(entityClass));

    }

    @Override
    protected <A extends ID> Boolean update(A entity, Set<String> changedFields) {
        A newEntity = (A) ReflectUtil.newInstance(entity.getClass());
        newEntity.setId(entity.getId());
        changedFields.forEach(field -> ReflectUtil.setFieldValue(newEntity, field, ReflectUtil.getFieldValue(entity, field)));
        SqlSession sqlSession = getSqlSession(entity.getClass());
        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
        map.put(Constants.ENTITY, newEntity);
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID.getMethod(), entity.getClass()), map));
        } finally {
            closeSqlSession(sqlSession, entity.getClass());
        }
    }

    @Override
    protected <A extends Versionable> Boolean update(A entity, Set<String> changedFields) {
        A newEntity = (A) ReflectUtil.newInstance(entity.getClass());
        newEntity.setId(entity.getId());
        newEntity.setVersion(entity.getVersion());
        changedFields.forEach(field -> ReflectUtil.setFieldValue(newEntity, field, ReflectUtil.getFieldValue(entity, field)));
        SqlSession sqlSession = getSqlSession(entity.getClass());
        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
        map.put(Constants.ENTITY, newEntity);
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID.getMethod(), entity.getClass()), map));
        } finally {
            closeSqlSession(sqlSession, entity.getClass());
        }
    }
}
