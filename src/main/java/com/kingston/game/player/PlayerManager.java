package com.kingston.game.player;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kingston.cache.CacheService;
import com.kingston.game.database.user.player.Player;
import com.kingston.orm.utils.DbUtils;

/**
 * 玩家业务管理器 
 * @author kingston
 */
public class PlayerManager extends CacheService<Long, Player> {

	private static PlayerManager instance = new PlayerManager();
	
	private ConcurrentMap<Long, Player> onlines = new ConcurrentHashMap<>();

	private PlayerManager() {}

	public static PlayerManager getInstance() {
		return instance;
	}

	public Player createNewPlayer(String name, byte job) {
		Player player = new Player();
		player.setName(name);
		player.setJob(job);
		//设为插入状态
		player.setInsert();

		return player;
	}


	@Override
	public Player load(Long playerId) throws Exception {
		String sql = "SELECT * FROM Player where Id = {0} ";
		sql = MessageFormat.format(sql, String.valueOf(playerId));
		Player player = DbUtils.queryOne(DbUtils.DB_USER, sql, Player.class);
		return player;
	}
	
	/**
	 * 添加进在线列表
	 * @param player
	 */
	public void add2Online(Player player) {
		this.onlines.put(player.getId(), player);
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

}
