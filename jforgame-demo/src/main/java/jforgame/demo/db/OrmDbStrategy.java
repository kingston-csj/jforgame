package jforgame.demo.db;

import jforgame.commons.persist.Entity;
import jforgame.commons.persist.SavingStrategy;
import jforgame.orm.entity.BaseEntity;

/**
 * 异步保存策略
 */
public class OrmDbStrategy implements SavingStrategy {

    @Override
    public void doSave(Entity<?> entity) throws Exception {
        BaseEntity<?> baseEntity = (BaseEntity<?>) entity;
        // 入库前准备
        baseEntity.beforeSave();
        // 根据实体的状态，执行不同的入库操作
        if (baseEntity.isSoftDeleted()) {
            DbUtils.executeDelete(baseEntity);
        } else if (baseEntity.isModified()) {
            DbUtils.executePreparedUpdate(baseEntity);
        } else if (baseEntity.isNew()) {
            DbUtils.executePreparedInsert(baseEntity);
        }
        // 入库后处理
        baseEntity.afterSave();
    }
}
