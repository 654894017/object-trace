package com.damon.object_trace.mybatis;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.damon.object_trace.DbRepositorySupport;
import com.damon.object_trace.ID;
import com.damon.object_trace.Versionable;
import com.damon.object_trace.exception.ObjectTraceException;
import com.damon.object_trace.utils.ReflectUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;

import java.util.Map;
import java.util.Set;

public class MybatisRepositorySupport extends DbRepositorySupport {

    @Override
    protected <A extends ID> Boolean delete(A item) {
        Class<?> clazz = item.getClass();
        SqlSession sqlSession = getSqlSession(clazz);
        try {
            return SqlHelper.retBool(sqlSession.delete(sqlStatement(SqlMethod.DELETE_BY_ID.getMethod(), clazz), item));
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

    /**
     * 未带版本号的更新
     *
     * @param entity
     * @param changedFields
     * @param <A>
     * @return
     */
    @Override
    protected <A extends ID> Boolean update(A entity, Set<String> changedFields) {
        A newEntity = (A) ReflectUtil.newInstance(entity.getClass());
        newEntity.setId(entity.getId());
        SqlSession sqlSession = getSqlSession(entity.getClass());
        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
        String primaryKey = ReflectUtils.getFieldNameByAnnotation(entity.getClass(), TableId.class);
        if (primaryKey == null) {
            throw new ObjectTraceException("Entity not found with the primary key annotation. entity : " + entity.getClass().getName());
        }
        UpdateWrapper<A> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(primaryKey, entity.getId());
        // 创建 UpdateWrapper 对象以指定更新条件
        changedFields.forEach(field -> updateWrapper.set(StrUtil.toUnderlineCase(field), ReflectUtil.getFieldValue(entity, field)));
        map.put(Constants.ENTITY, newEntity);
        map.put(Constants.WRAPPER, updateWrapper);
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE.getMethod(), entity.getClass()), map));
        } finally {
            closeSqlSession(sqlSession, entity.getClass());
        }
    }

    /**
     * 带版本version的安全更新
     *
     * @param entity
     * @param changedFields
     * @param <A>
     * @return
     */
    @Override
    protected <A extends Versionable> Boolean update(A entity, Set<String> changedFields) {
        A newEntity = (A) ReflectUtil.newInstance(entity.getClass());
        newEntity.setId(entity.getId());
        newEntity.setVersion(entity.getVersion());
        SqlSession sqlSession = getSqlSession(entity.getClass());
        Map<String, Object> map = CollectionUtils.newHashMapWithExpectedSize(1);
        // 创建 UpdateWrapper 对象以指定更新条件
        String primaryKey = ReflectUtils.getFieldNameByAnnotation(entity.getClass(), TableId.class);
        if (primaryKey == null) {
            throw new ObjectTraceException("Entity not found with the primary key annotation. entity : " + entity.getClass().getName());
        }
        UpdateWrapper<A> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(primaryKey, entity.getId());
        changedFields.forEach(field -> updateWrapper.set(StrUtil.toUnderlineCase(field), ReflectUtil.getFieldValue(entity, field)));
        map.put(Constants.ENTITY, newEntity);
        map.put(Constants.WRAPPER, updateWrapper);
        try {
            return SqlHelper.retBool(sqlSession.update(sqlStatement(SqlMethod.UPDATE.getMethod(), entity.getClass()), map));
        } finally {
            closeSqlSession(sqlSession, entity.getClass());
        }
    }
}
