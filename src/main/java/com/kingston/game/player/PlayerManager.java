package com.kingston.game.player;

import java.text.MessageFormat;

import com.kingston.cache.CacheService;
import com.kingston.game.database.user.player.Player;
import com.kingston.orm.utils.DbUtils;

/**
 * 玩家业务管理器 
 * @author kingston
 */
public class PlayerManager extends CacheService<Long, Player> {

	private static PlayerManager instance = new PlayerManager();

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

}
