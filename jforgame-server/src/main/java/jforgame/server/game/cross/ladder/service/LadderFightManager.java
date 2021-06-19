package jforgame.server.game.cross.ladder.service;

import jforgame.server.ServerConfig;

/**
 * 天梯战斗服业务处理
 *
 */
public class LadderFightManager {

	private static volatile LadderFightManager self = new LadderFightManager();
	
	public static LadderFightManager getInstance() {
		return self;
	}
	
	public void init() {
		ServerConfig config = ServerConfig.getInstance();
		if (!config.isCenter()) {
			return;
		}
	}
	

}
