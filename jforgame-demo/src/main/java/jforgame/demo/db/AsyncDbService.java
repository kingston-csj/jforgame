package jforgame.demo.db;

import jforgame.commons.persist.DbService;
import jforgame.commons.persist.Entity;
import jforgame.commons.persist.PersistContainer;
import jforgame.commons.persist.QueueContainer;
import jforgame.commons.persist.SavingStrategy;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.orm.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户数据异步持久化的服务
 */
public class AsyncDbService implements DbService {

    private static Logger logger = LoggerFactory.getLogger(AsyncDbService.class);

    private static volatile AsyncDbService instance = new AsyncDbService();

    public static AsyncDbService getInstance() {
        return instance;
    }

    private SavingStrategy savingStrategy = new OrmDbStrategy();
    // 玩家持久化容器
    private PersistContainer playerWorker = new QueueContainer("player", savingStrategy);
    // 非玩家持久化容器
    private PersistContainer commonWorker = new QueueContainer("common", savingStrategy);

    /**
     * 数据实体持久化到数据库
     *
     * @param entity
     */
    public void saveToDb(Entity<?> entity) {
        if (entity instanceof PlayerEnt) {
            playerWorker.receive(entity);
        } else {
            commonWorker.receive(entity);
        }
    }

    /**
     * 删除数据
     *
     * @param entity
     */
    public void deleteFromDb(Entity<?> entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        baseEntity.markAsSoftDeleted();
        saveToDb(entity);
    }

    /**
     * 增加更新表字段
     * 仅更新部分字段
     *
     * @param entity
     * @param columns
     */
    public void saveColumns(BaseEntity entity, String... columns) {
        entity.addModifiedColumn(columns);
        saveToDb(entity);
    }


    public void shutDown() {
        playerWorker.shutdownGraceful();
        commonWorker.shutdownGraceful();
        LoggerUtils.error("[Db4Common] 执行全部命令后关闭");
    }

}
