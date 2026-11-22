package jforgame.demo.framework.cache;

import jforgame.cache.DatabaseAccessor;
import jforgame.commons.persist.Entity;
import jforgame.demo.framework.db.DbUtils;
import jforgame.orm.core.OrmBridge;
import jforgame.orm.core.OrmProcessor;
import jforgame.orm.entity.BaseEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * 基于 jforgame-orm 和 demo 用户数据源的 {@link DatabaseAccessor} 实现。
 * 实体必须使用 JPA 注解并继承 {@link BaseEntity}，且在使用前完成 ORM 元数据初始化。
 */
public final class JdbcDatabaseAccessor implements DatabaseAccessor {

    @Override
    public <E> E load(Class<E> entityClass, Object id) {
        OrmBridge bridge = requireBridge(entityClass);
        List<String> primaryKeys = bridge.getPrimaryKeyProperties();
        if (primaryKeys.size() != 1) {
            throw new IllegalStateException(entityClass.getName() + " must have exactly one primary key to be cached");
        }
        String property = primaryKeys.get(0);
        String override = bridge.getOverrideProperty(property);
        String column = override != null ? override : property;
        String sql = "SELECT * FROM " + bridge.getTableName() + " WHERE " + column + " = ?";
        try {
            return DbUtils.queryOne(DbUtils.DB_USER, sql, entityClass, id);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load " + entityClass.getName() + " with id " + id, e);
        }
    }

    @Override
    public void saveOrUpdate(Entity<?> entity) {
        BaseEntity<?> baseEntity = requireBaseEntity(entity);
        baseEntity.beforeSave();
        try {
            if (baseEntity.isSoftDeleted()) {
                DbUtils.executeDelete(baseEntity);
            } else if (baseEntity.isModified()) {
                DbUtils.executePreparedUpdate(baseEntity);
            } else if (baseEntity.isNew()) {
                DbUtils.executePreparedInsert(baseEntity);
            } else {
                return;
            }
            baseEntity.afterSave();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save " + entity.getKey(), e);
        }
    }

    @Override
    public void remove(Entity<?> entity) {
        BaseEntity<?> baseEntity = requireBaseEntity(entity);
        try {
            DbUtils.executeDelete(baseEntity);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to remove " + entity.getKey(), e);
        }
    }

    private static OrmBridge requireBridge(Class<?> entityClass) {
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entityClass);
        if (bridge == null) {
            throw new IllegalStateException(entityClass.getName() + " is not registered in OrmProcessor, "
                    + "please initialize jforgame-orm first");
        }
        return bridge;
    }

    private static BaseEntity<?> requireBaseEntity(Entity<?> entity) {
        if (!(entity instanceof BaseEntity)) {
            throw new IllegalArgumentException(entity.getClass().getName() + " must extend " + BaseEntity.class.getName());
        }
        return (BaseEntity<?>) entity;
    }
}
