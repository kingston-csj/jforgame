package com.kingston.jforgame.match.game.ladder.service;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import com.kingston.jforgame.match.game.ladder.model.FightServerNode;

/**
 * 天梯匹配中心服务
 *
 */
public class LadderCenterManager {

	private static LadderCenterManager self = new LadderCenterManager();

	private ScheduledExecutorService scheuledService = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("ladder-scheuled"));

	/**
	 * 
	 * 所有活跃的战斗服{@link FightServerNode}列表
	 */
	private CopyOnWriteArrayList<FightServerNode> fightServers = new CopyOnWriteArrayList<>();

	public static LadderCenterManager getInstance() {
		return self;
	}

	public void init() {
		Runnable task = () -> {
			for (FightServerNode node : fightServers) {
				if (node.isOverTime()) {
					fightServers.remove(node);
				}
			}
		};
		scheuledService.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
	}

	public void updateFightServer(int serverId, String ip, int port) {
		boolean addedAlready = false;
		for (FightServerNode node : fightServers) {
			if (node.getServerId() == serverId) {
				addedAlready = true;
				node.updateLastHeatBeat();
			}
		}
		if (!addedAlready) {
			FightServerNode node = new FightServerNode(serverId, ip, port);
			fightServers.add(node);
		}
	}

}
