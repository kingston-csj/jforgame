package jforgame.demo.db;

import jforgame.orm.asyncdb.SavingStrategy;
import jforgame.orm.entity.BaseEntity;

/**
 * 异步保存策略
 *
 * @author kinson
 */
public class OrmDbStrategy implements SavingStrategy {

    @Override
    public void doSave(BaseEntity<?> entity) throws Exception {
        // 入库前准备
        entity.beforeSave();
        // 根据实体的状态，执行不同的入库操作
        if (entity.isDelete()) {
            DbUtils.executeDelete(entity);
        } else if (entity.isUpdate()) {
            DbUtils.executePreparedUpdate(entity);
        } else if (entity.isInsert()) {
            DbUtils.executePreparedInsert(entity);
        }
        // 入库后处理
        entity.afterLoad();
    }
}
