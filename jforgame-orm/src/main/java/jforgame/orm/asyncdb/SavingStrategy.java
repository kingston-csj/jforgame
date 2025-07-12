package jforgame.orm.asyncdb;

import jforgame.orm.entity.BaseEntity;

/**
 * 持久化策略
 */
public interface SavingStrategy {

    /**
     * 真正执行入库工作，
     *
     * @param entity
     */
    void doSave(BaseEntity<?> entity) throws Exception;


}
