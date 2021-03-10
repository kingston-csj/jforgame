package jforgame.server.game.player;

import jforgame.server.game.GameContext;
import jforgame.server.net.SessionProperties;
import jforgame.common.utils.NumberUtil;
import jforgame.server.cache.BaseCacheService;
import jforgame.server.db.DbService;
import jforgame.server.db.DbUtils;
import jforgame.server.game.accout.entity.AccountEnt;
import jforgame.server.game.core.MessagePusher;
import jforgame.server.game.core.SystemParameters;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.game.login.model.Platform;
import jforgame.server.game.player.events.PlayerLogoutEvent;
import jforgame.server.game.player.message.res.ResCreateNewPlayer;
import jforgame.server.game.player.message.res.ResKickPlayer;
import jforgame.server.game.player.model.AccountProfile;
import jforgame.server.game.player.model.PlayerProfile;
import jforgame.server.listener.EventDispatcher;
import jforgame.server.listener.EventType;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.utils.IdGenerator;
import jforgame.socket.IdSession;
import jforgame.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 玩家业务管理器
 * 
 * @author kinson
 */
public class PlayerManager extends BaseCacheService<Long, PlayerEnt> {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ConcurrentMap<Long, PlayerEnt> onlines = new ConcurrentHashMap<>();

	/** 全服所有角色的简况 */
	private ConcurrentMap<Long, PlayerProfile> playerProfiles = new ConcurrentHashMap<>();

	/** 全服所有账号的简况 */
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
			LoggerUtils.error("", e);
		}
	}

	private void addPlayerProfile(PlayerProfile baseInfo) {
		playerProfiles.put(baseInfo.getId(), baseInfo);

		long accountId = baseInfo.getAccountId();
		// 必须将account加载并缓存
		AccountEnt account = GameContext.accountManager.get(accountId);
		accountProfiles.putIfAbsent(accountId, new AccountProfile());
		AccountProfile accountProfile = accountProfiles.get(accountId);
		accountProfile.addPlayerProfile(baseInfo);
	}

	public AccountProfile getAccountProfiles(long accountId) {
		AccountProfile accountProfile = accountProfiles.get(accountId);
		if (accountProfile != null) {
			return accountProfile;
		}
		AccountEnt account = GameContext.accountManager.get(accountId);
		if (account != null) {
			accountProfile = new AccountProfile();
			accountProfile.setAccountId(accountId);
			accountProfiles.putIfAbsent(accountId, accountProfile);
		}
		return accountProfile;
	}

	public void addAccountProfile(AccountEnt accountEnt) {
		long accountId = accountEnt.getId();
		if (accountProfiles.containsKey(accountId)) {
			throw new RuntimeException("账号重复-->" + accountId);
		}
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setAccountId(accountId);
		accountProfiles.put(accountId, accountProfile);
	}

	public List<PlayerProfile> getPlayersBy(long accountId) {
		AccountProfile account = accountProfiles.get(accountId);
		if (account == null) {
			return null;
		}
		return account.getPlayers();
	}

	public void createNewPlayer(IdSession session, String name) {
		long accountId = (long) session.getAttribute(SessionProperties.ACCOUNT);
		PlayerEnt player = new PlayerEnt();
		player.setId(IdGenerator.getNextId());
		player.setName(name);
		player.setAccountId(accountId);
		player.setPlatform(Platform.ANDROID);

		long playerId = player.getId();
		// 手动放入缓存
		super.put(playerId, player);

		DbService.getInstance().insertOrUpdate(player);

		PlayerProfile baseInfo = new PlayerProfile();
		baseInfo.setAccountId(accountId);
		baseInfo.setId(playerId);
		baseInfo.setLevel(player.getLevel());
		baseInfo.setJob(player.getJob());
		baseInfo.setName(player.getName());

		ResCreateNewPlayer response = new ResCreateNewPlayer();
		response.setPlayerId(playerId);
		MessagePusher.pushMessage(session, response);

		GameContext.loginManager.handleSelectPlayer(session, playerId);
	}

	/**
	 * 从用户表里读取玩家数据
	 */
	@Override
	public PlayerEnt load(Long playerId) throws Exception {
		String sql = "SELECT * FROM PlayerEnt where Id = ? ";
//		sql = MessageFormat.format(sql, String.valueOf(playerId));
		PlayerEnt player = DbUtils.queryOneById(DbUtils.DB_USER, sql, PlayerEnt.class, String.valueOf(playerId));
		if (player != null) {
			player.doAfterInit();
		}
		return player;
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
