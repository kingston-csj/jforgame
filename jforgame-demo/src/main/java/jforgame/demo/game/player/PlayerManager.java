package jforgame.demo.game.player;

import jforgame.commons.util.NumberUtil;
import jforgame.demo.cache.BaseCacheService;
import jforgame.demo.db.AsyncDbService;
import jforgame.demo.db.DbUtils;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.core.MessagePusher;
import jforgame.demo.game.core.SystemParameters;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.player.events.PlayerLogoutEvent;
import jforgame.demo.game.player.message.ResCreateNewPlayer;
import jforgame.demo.game.player.message.ResKickPlayer;
import jforgame.demo.game.player.model.AccountProfile;
import jforgame.demo.game.player.model.PlayerProfile;
import jforgame.demo.listener.EventDispatcher;
import jforgame.demo.listener.EventType;
import jforgame.demo.socket.SessionManager;
import jforgame.demo.utils.IdGenerator;
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
public class PlayerManager extends BaseCacheService<Long, PlayerEnt> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ConcurrentMap<Long, PlayerEnt> onlines = new ConcurrentHashMap<>();

    /**
     * 全服所有角色的简况
     */
    private ConcurrentMap<Long, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();

    /**
     * 全服所有账号的简况
     */
    private ConcurrentMap<Long, AccountProfile> accountProfiles = new ConcurrentHashMap<>();

    public void loadAllPlayerProfiles() {
        String sql = "SELECT id, accountId,name,level,job FROM playerent";
        try {
            List<Map<String, Object>> result = DbUtils.queryMapList(DbUtils.DB_USER, sql);
            for (Map<String, Object> record : result) {
                PlayerProfile baseInfo = new PlayerProfile();
                baseInfo.setAccountId(NumberUtil.longValue(record.get("accountId")));
                baseInfo.setId(NumberUtil.longValue(record.get("id")));
                baseInfo.setJob(NumberUtil.intValue(record.get("job")));
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

    public List<PlayerProfile> getPlayersBy(long accountId) {
        AccountProfile account = accountProfiles.get(accountId);
        if (account == null) {
            return null;
        }
        return account.getPlayers();
    }

    private void createNewPlayer(IdSession session, String name) {
        PlayerEnt player = new PlayerEnt();
        player.setId(IdGenerator.getNextId());
        player.setName(name);

        long playerId = player.getId();
        // 手动放入缓存
        super.put(playerId, player);

        AsyncDbService.getInstance().saveToDb(player);

        PlayerProfile baseInfo = new PlayerProfile();
        baseInfo.setId(playerId);
        baseInfo.setLevel(player.getLevel());
        baseInfo.setJob(player.getJob());
        baseInfo.setName(player.getName());

        ResCreateNewPlayer response = new ResCreateNewPlayer();
        response.setPlayerId(playerId);
        MessagePusher.pushMessage(session, response);
    }

    public void handleAccountLogin(IdSession session, long playerId) {
        PlayerEnt player = GameContext.playerManager.get(playerId);
        if (player != null) {
            //绑定session与玩家id
            session.setAttribute(IdSession.ID, playerId);
            //加入在线列表
            GameContext.playerManager.add2Online(player);

            player.setLevel(999);
            GameContext.playerManager.save(player);

            SessionManager.INSTANCE.registerNewPlayer(playerId, session);
            //检查日重置
            GameContext.playerManager.checkDailyReset(player);
        } else {
            createNewPlayer(session, "" + playerId);
        }
    }

    /**
     * 从用户表里读取玩家数据
     */
    @Override
    public PlayerEnt load(Long playerId) throws Exception {
        String sql = "SELECT * FROM playerent where Id = ? ";
        return DbUtils.queryOne(DbUtils.DB_USER, sql, PlayerEnt.class, playerId);
    }

    public PlayerEnt getOnlinePlayer(long playerId) {
        if (!onlines.containsKey(playerId)) {
            return null;
        }
        return get(playerId);
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
        save(player);
    }

    public void playerLogout(long playerId) {
        PlayerEnt player = GameContext.playerManager.get(playerId);
        if (player == null) {
            return;
        }
        logger.info("角色[{}]退出游戏", playerId);

        EventDispatcher.getInstance().fireEvent(new PlayerLogoutEvent(EventType.LOGOUT, playerId));
    }

    public void kickPlayer(long playerId) {
        PlayerEnt player = GameContext.playerManager.getOnlinePlayer(playerId);
        if (player == null) {
            return;
        }
        removeFromOnline(player);
        IdSession session = SessionManager.INSTANCE.getSessionBy(playerId);
        MessagePusher.pushMessage(session, new ResKickPlayer());
//		session.close(false);
    }

}
