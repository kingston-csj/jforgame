package jforgame.demo.db;

import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.orm.asyncdb.PersistContainer;
import jforgame.orm.asyncdb.QueueContainer;
import jforgame.orm.asyncdb.SavingStrategy;
import jforgame.orm.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 用户数据异步持久化的服务
 *
 */
public class DbService {

    private static Logger logger = LoggerFactory.getLogger(DbService.class);

    private static volatile DbService instance = new DbService();

    public static DbService getInstance() {
        return instance;
    }

    private SavingStrategy savingStrategy = new OrmDbStrategy();
    // 玩家持久化容器
    private PersistContainer playerWorker = new QueueContainer("player", savingStrategy);
    // 非玩家持久化容器
    private PersistContainer commonWorker = new QueueContainer("common", savingStrategy);

    /**
     * 自动插入或者更新数据
     *
     * @param entity
     */
    public void saveToDb(BaseEntity<?> entity) {
        // 防止重复添加
        if (entity instanceof PlayerEnt) {
            playerWorker.receive(entity);
        } else {
            commonWorker.receive(entity);
        }
    }

    /**
     * 仅更新部分字段
     *
     * @param entity
     * @param columns
     */
    public void saveColumns(BaseEntity entity, String... columns) {
        entity.savingColumns().add(Arrays.stream(columns).collect(Collectors.joining()));
        saveToDb(entity);
    }

    /**
     * 删除数据
     *
     * @param entity
     */
    public void delete(BaseEntity entity) {
        entity.setDelete();
        saveToDb(entity);
    }


    public void shutDown() {
        playerWorker.shutdownGraceful();
        commonWorker.shutdownGraceful();
        LoggerUtils.error("[Db4Common] 执行全部命令后关闭");
    }

}
