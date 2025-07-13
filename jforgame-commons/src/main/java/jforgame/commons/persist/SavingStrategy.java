package jforgame.commons.persist;

/**
 * 持久化策略
 */
public interface SavingStrategy {

    /**
     * 真正执行入库工作，实现类可自行选择持久化方式，如：spring data jpa, jforgame-orm, mybatics等等
     *
     * @param entity
     */
    void doSave(Entity<?> entity) throws Exception;


}
