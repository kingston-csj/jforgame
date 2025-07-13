package jforgame.commons.persist;


/**
 * 持久化容器
 * 主要有四种类型：
 * 1. 基于队列的持久化容器，见{@link QueueContainer}
 * 2. 基于延迟的持久化容器，见{@link DelayContainer}
 * 3. 基于时间周期性调度(cron)的持久化容器， 需要引入quartz库，因此不默认提供， 可自行实现
 * 4. 以上3种的自由组合，参考{@link QueueContainerGroup}
 */
public interface PersistContainer {

    /**
     * 接收实体
     *
     * @param entity
     */
    void receive(Entity<?> entity);

    /**
     * 优雅退出，会保证所有等待队列的数据都会跑完
     */
    void shutdownGraceful();

    /**
     * 当前等待入库的队列大小
     *
     * @return size
     */
    int size();

}