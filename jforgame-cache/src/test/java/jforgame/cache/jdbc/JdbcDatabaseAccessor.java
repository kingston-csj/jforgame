package jforgame.cache.jdbc;

import jforgame.cache.DatabaseAccessor;
import jforgame.commons.persist.Entity;
import jforgame.orm.core.OrmBridge;
import jforgame.orm.core.OrmProcessor;
import jforgame.orm.core.OrmTemplate;
import jforgame.orm.entity.BaseEntity;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Example {@link DatabaseAccessor} implementation backed by jforgame-orm and a JDBC {@link DataSource}.
 * Entity classes must be annotated {@code javax.persistence.Entity} and extend {@link BaseEntity}.
 *
 * <p>This class lives in test sources on purpose: it demonstrates how the business layer can
 * implement {@link DatabaseAccessor} without making jforgame-cache depend on any persistence framework.
 */
public final class JdbcDatabaseAccessor implements DatabaseAccessor {

    private final OrmTemplate ormTemplate;

    public JdbcDatabaseAccessor(DataSource dataSource) {
        this.ormTemplate = new OrmTemplate(dataSource);
    }

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
            return ormTemplate.queryOne(sql, entityClass, id);
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
                ormTemplate.executeDelete(baseEntity);
            } else if (baseEntity.isModified()) {
                ormTemplate.executeUpdate(baseEntity);
            } else if (baseEntity.isNew()) {
                ormTemplate.executeInsert(baseEntity);
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
            ormTemplate.executeDelete(baseEntity);
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
