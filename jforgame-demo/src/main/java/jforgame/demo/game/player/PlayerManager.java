package jforgame.demo.game.player;

import jforgame.cache.BaseEntityCacheService;
import jforgame.commons.util.NumberUtil;
import jforgame.demo.framework.cache.GuavaCacheStore;
import jforgame.demo.framework.cache.JdbcDatabaseAccessor;
import jforgame.demo.framework.db.DbUtils;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.core.MessagePusher;
import jforgame.demo.game.core.SystemParameters;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.logger.LoggerFunction;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.demo.game.player.events.PlayerLogoutEvent;
import jforgame.demo.game.player.message.ResPlayerLogin;
import jforgame.demo.game.player.message.PushPlayerKick;
import jforgame.demo.game.player.model.PlayerProfile;
import jforgame.demo.framework.socket.SessionManager;
import jforgame.socket.core.session.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 玩家业务管理器
 */
public class PlayerManager extends BaseEntityCacheService<PlayerEnt> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public PlayerManager() {
        super(PlayerEnt.class, new JdbcDatabaseAccessor(), new GuavaCacheStore());
    }

    private final ConcurrentMap<Long, PlayerEnt> onlines = new ConcurrentHashMap<>();

    /**
     * 全服所有角色的简况
     */
    private final ConcurrentMap<Long, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();

    public void loadAllPlayerProfiles() {
        String sql = "SELECT id,name,level,job FROM playerent";
        try {
            List<Map<String, Object>> result = DbUtils.queryMapList(DbUtils.DB_USER, sql);
            for (Map<String, Object> record : result) {
                PlayerProfile baseInfo = new PlayerProfile();
                baseInfo.setId(NumberUtil.longValue(record.get("id")));
                baseInfo.setName((String) record.get("name"));
                addPlayerProfile(baseInfo);
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    private void addPlayerProfile(PlayerProfile baseInfo) {
        playerProfiles.put(baseInfo.getId(), baseInfo);
    }

    private PlayerEnt createNewPlayer(long playerId, String name) {
        PlayerEnt player = getOrCreate(playerId, () -> {
            PlayerEnt playerEnt = new PlayerEnt();
            playerEnt.setId(playerId);
            playerEnt.setName(name);
            return playerEnt;
        });

        PlayerProfile baseInfo = new PlayerProfile();
        baseInfo.setId(playerId);
        baseInfo.setLevel(player.getLevel());
        baseInfo.setName(player.getName());
        addPlayerProfile(baseInfo);

        return player;
    }


    public PlayerEnt getPlayer(long playerId) {
        return getOrCreate(playerId, () -> {
            PlayerEnt playerEnt = new PlayerEnt();
            playerEnt.setId(playerId);
            return playerEnt;
        });
    }


    public void handlePlayerLogin(IdSession session, long playerId) {
        PlayerEnt player = GameContext.playerManager.getEntity(playerId);
        LoggerUtils.logPlayer(LoggerFunction.Net, String.valueOf(playerId), "model", "login");
        if (player != null) {
            //绑定session与玩家id
            session.setOwnerId(String.valueOf(playerId));
            //加入在线列表
            GameContext.playerManager.add2Online(player);

            player.setLevel(999);
            GameContext.playerManager.putEntity(player);

            SessionManager.INSTANCE.registerNewPlayer(playerId, session);
            //检查日重置
            GameContext.playerManager.checkDailyReset(player);
        } else {
            createNewPlayer(playerId, playerId + "");
            ResPlayerLogin response = new ResPlayerLogin();
            response.setPlayerId(playerId);
            MessagePusher.pushMessage(session, response);
        }
    }

    public PlayerEnt getOnlinePlayer(long playerId) {
        if (!onlines.containsKey(playerId)) {
            return null;
        }
        return getEntity(playerId);
    }

    /**
     * 添加进在线列表
     *
     * @param player
     */
    public void add2Online(PlayerEnt player) {
        this.onlines.put(player.getId(), player);
    }

    public boolean isOnline(long playerId) {
        return this.onlines.containsKey(playerId);
    }

    /**
     * 返回在线玩家列表的拷贝
     *
     * @return
     */
    public ConcurrentMap<Long, PlayerEnt> getOnlinePlayers() {
        return new ConcurrentHashMap<>(this.onlines);
    }

    /**
     * 从在线列表中移除
     *
     * @param player
     */
    public void removeFromOnline(PlayerEnt player) {
        if (player != null) {
            this.onlines.remove(player.getId());
        }
    }

    public void checkDailyReset(PlayerEnt player) {
        long resetTimestamp = SystemParameters.dailyResetTimestamp;
        if (player.getLastDailyReset() < resetTimestamp) {
            player.setLastDailyReset(SystemParameters.dailyResetTimestamp);
            onDailyReset(player);
        }
    }

    /**
     * 各个模块的业务日重置
     *
     * @param player
     */
    private void onDailyReset(PlayerEnt player) {
        putEntity(player);
    }

    public void playerLogout(long playerId) {
        PlayerEnt player = GameContext.playerManager.getEntity(playerId);
        if (player == null) {
            return;
        }
        logger.info("角色[{}]退出游戏", playerId);

        GameContext.eventBus.post(new PlayerLogoutEvent(player));
    }

    public void kickPlayer(long playerId) {
        PlayerEnt player = GameContext.playerManager.getOnlinePlayer(playerId);
        if (player == null) {
            return;
        }
        removeFromOnline(player);
        IdSession session = SessionManager.INSTANCE.getSessionBy(playerId);
        MessagePusher.pushMessage(session, new PushPlayerKick());
    }

}
