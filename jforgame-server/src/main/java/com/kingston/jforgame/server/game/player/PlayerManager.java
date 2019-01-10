package com.kingston.jforgame.server.game.player;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.server.cache.BaseCacheService;
import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.core.SystemParameters;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.player.events.PlayerLogoutEvent;
import com.kingston.jforgame.server.game.player.message.ResCreateNewPlayerMessage;
import com.kingston.jforgame.server.game.player.message.ResKickPlayerMessage;
import com.kingston.jforgame.server.listener.EventDispatcher;
import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.utils.IdGenerator;
import com.kingston.jforgame.socket.message.MessagePusher;
import com.kingston.jforgame.socket.session.SessionManager;

/**
 * 玩家业务管理器
 * @author kingston
 */
public class PlayerManager extends BaseCacheService<Long, Player> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static PlayerManager instance = new PlayerManager();

	private ConcurrentMap<Long, Player> onlines = new ConcurrentHashMap<>();

	public static PlayerManager getInstance() {
		return instance;
	}

	public void createNewPlayer(IoSession session, String name) {
		Player player = new Player();
		player.setId(IdGenerator.getNextId());
		player.setName(name);

		long playerId = player.getId();
		// 手动放入缓存
		super.put(playerId, player);
		
		DbService.getInstance().add2Queue(player);

		ResCreateNewPlayerMessage response = new ResCreateNewPlayerMessage();
		response.setPlayerId(playerId);
		MessagePusher.pushMessage(session, response);

		LoginManager.getInstance().handleSelectPlayer(session, playerId);
	}


	/**
	 * 从用户表里读取玩家数据
	 */
	@Override
	public Player load(Long playerId) throws Exception {
		String sql = "SELECT * FROM Player where Id = {0} ";
		sql = MessageFormat.format(sql, String.valueOf(playerId));
		Player player = DbUtils.queryOne(DbUtils.DB_USER, sql, Player.class);
		player.doAfterInit();
		return player;
	}

	public Player getOnlinePlayer(long playerId) {
		if (!onlines.containsKey(playerId)) {
			return null;
		}
		return get(playerId);
	}

	/**
	 * 添加进在线列表
	 * @param player
	 */
	public void add2Online(Player player) {
		this.onlines.put(player.getId(), player);
	}
	
	public boolean isOnline(long playerId) {
		return this.onlines.containsKey(playerId);
	}

	/**
	 * 返回在线玩家列表的拷贝
	 * @return
	 */
	public ConcurrentMap<Long, Player> getOnlinePlayers() {
		return new ConcurrentHashMap<>(this.onlines);
	}

	/**
	 * 从在线列表中移除
	 * @param player
	 */
	public void removeFromOnline(Player player) {
		if (player != null) {
			this.onlines.remove(player.getId());
		}
	}

	public void checkDailyReset(Player player) {
		long resetTimestamp = SystemParameters.dailyResetTimestamp;
		if (player.getLastDailyReset() < resetTimestamp) {
			player.setLastDailyReset(SystemParameters.dailyResetTimestamp);
			onDailyReset(player);
		}
	}

	/**
	 * 各个模块的业务日重置
	 * @param player
	 */
	private void onDailyReset(Player player) {

	}
	
	public void playerLogout(long playerId) {
		Player player = PlayerManager.getInstance().get(playerId);
		if (player == null) {
			return;
		}
		logger.info("角色[{}]退出游戏", playerId);
		
		EventDispatcher.getInstance().fireEvent(new PlayerLogoutEvent(EventType.LOGOUT, playerId));
	}

	public void kickPlayer(long playerId) {
		Player player = PlayerManager.getInstance().getOnlinePlayer(playerId);
		if (player == null) {
			return;
		}
		removeFromOnline(player);
		IoSession session = SessionManager.INSTANCE.getSessionBy(playerId);
		MessagePusher.pushMessage(session, new ResKickPlayerMessage());
		session.close(false);
	}

}
