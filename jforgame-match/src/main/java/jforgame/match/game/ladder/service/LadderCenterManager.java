package jforgame.match.game.ladder.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.common.utils.ConcurrentHashSet;
import jforgame.match.game.ladder.model.FightServerNode;
import jforgame.match.game.ladder.model.LadderMatchVo;
import jforgame.match.game.ladder.model.PlayerApplyRecord;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * 天梯匹配中心服务
 *
 */
public class LadderCenterManager {

	private static LadderCenterManager self = new LadderCenterManager();

	private ScheduledExecutorService scheduledService = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("ladder-scheduled"));

	/**
	 * 
	 * 所有活跃的战斗服{@link FightServerNode}列表
	 */
	private CopyOnWriteArrayList<FightServerNode> fightServers = new CopyOnWriteArrayList<>();

	private ConcurrentMap<Integer, Set<LadderMatchVo>> serverMatchResults = new ConcurrentHashMap<>();
	
	private AtomicInteger matchIdFactory = new AtomicInteger();
	
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
		scheduledService.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
	}

	public void updateFightServer(int serverId, String ip, int port) {
		boolean addedAlready = false;
		for (FightServerNode node : fightServers) {
			if (node.getServerId() == serverId) {
				addedAlready = true;
				node.updateLastHeatBeat();
				break;
			}
		}
		if (!addedAlready) {
			FightServerNode node = new FightServerNode(serverId, ip, port);
			fightServers.add(node);
		}
	}
	
	public void apply(PlayerApplyRecord record) {
		int matchId = matchIdFactory.getAndIncrement();
		// 战斗服分配负载均衡
		int fightServerId = matchId % fightServers.size();
		FightServerNode fightServer = fightServers.get(fightServerId);
		LadderMatchVo matchVo = new LadderMatchVo();
		matchVo.setFightServerIp(fightServer.getIp());
		matchVo.setFightServerPort(fightServer.getPort());
		Set<LadderMatchVo> serverResults = serverMatchResults.getOrDefault(record.getFromServerId(), new ConcurrentHashSet<>());
		serverMatchResults.putIfAbsent(record.getFromServerId(), serverResults);
		serverResults.add(matchVo);
	}
	
	public Set<LadderMatchVo> queryMatchResult(int serverId) {
		Set<LadderMatchVo> result = new HashSet<>();
		result.addAll(serverMatchResults.getOrDefault(serverId, new HashSet<>()));
		
		return result;
	}
 
}
