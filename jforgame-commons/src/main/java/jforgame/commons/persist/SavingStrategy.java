package jforgame.commons.persist;

/**
 * 持久化策略
 */
public interface SavingStrategy {

    /**
     * 真正执行入库工作，实现类可自行选择持久化方式，如：spring data jpa, jforgame-orm, mybatics等等
     * 当持久化失败，客户端代码必须捕获该异常，视情况选择将对象重新加入等待队列，避免数据丢失
     * @param entity 实体对象
     * @throws Exception 保存过程中可能抛出的异常
     *
     */
    void doSave(Entity<?> entity) throws Exception;


}
