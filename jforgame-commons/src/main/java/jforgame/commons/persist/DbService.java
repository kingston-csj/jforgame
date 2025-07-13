package jforgame.commons.persist;

/**
 * 数据持久化服务
 */
public interface DbService {

    /**
     * 数据实体持久化到数据库
     * 该接口会保证无论表记录是否已存在于数据库
     * 若存在则执行更新操作，否则执行插入动作
     * @param entity 待入库的实体
     */
    void saveToDb(Entity<?> entity);

    /**
     * 删除数据
     *
     * @param entity 待删除的实体
     */
    void deleteFromDb(Entity<?> entity);

    /**
     * 关闭服务
     * 如果是异步持久化，需要保存所有缓存数据写入到数据库
     */
    void shutDown();

}
