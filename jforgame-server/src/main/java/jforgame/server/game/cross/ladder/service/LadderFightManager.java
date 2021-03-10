package jforgame.server.game.cross.ladder.service;

import jforgame.server.ServerConfig;
import jforgame.server.match.AbstractMatchMessage;
import jforgame.server.match.MatchHttpUtil;
import jforgame.server.game.cross.ladder.message.F2M_HeatBeat;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.thread.SchedulerManager;

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
		if (!config.isFight()) {
			return;
		}
		heatBeatTimer();
	}
	
	private void heatBeatTimer() {
		Runnable task = () -> {
			F2M_HeatBeat heatBeat = new F2M_HeatBeat();
			try {
				AbstractMatchMessage response = MatchHttpUtil.submit(heatBeat);
			} catch (Exception e) {
				LoggerUtils.error("发送心跳失败", e);
			}
		};
		SchedulerManager.scheduleAtFixedRate(task, 0, 10000);
	}
	
}
