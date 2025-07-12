package jforgame.orm.asyncdb;

import jforgame.orm.entity.BaseEntity;

/**
 * 持久化容器
 */
@SuppressWarnings("all")
public interface PersistContainer {

    /**
     * 接收实体
     *
     * @param entity
     */
    void receive(BaseEntity<?> entity);

    /**
     * 优雅退出，会保证所有等待队列的数据都会跑完
     */
    void shutdownGraceful();

    /**
     * 获取保存策略
     *
     * @return
     */
    SavingStrategy getSavingStrategy();
}