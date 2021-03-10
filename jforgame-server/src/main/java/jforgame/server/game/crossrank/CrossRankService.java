package jforgame.server.game.crossrank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jforgame.server.game.crossrank.impl.CrossLevelRank;
import jforgame.server.redis.RedisCluster;
import jforgame.server.redis.RedisCodecHelper;

import redis.clients.jedis.Tuple;

public class CrossRankService {

	private static CrossRankService instance;

	private RedisCluster cluster = RedisCluster.INSTANCE;

	private Map<Integer, Class<? extends AbstractCrossRank>> rank2Class = new HashMap<>();

	public static CrossRankService getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (CrossRankService.class) {
			if (instance == null) {
				instance = new CrossRankService();
				instance.init();
			}
		}
		return instance;
	}

	private void init() {
		rank2Class.put(CrossRankKinds.LEVEL, CrossLevelRank.class);
	}

	public void addRank(CrossRank rank) {
		String key = rank.buildRankKey();
		String member = buildRankMember(rank.getPlayerId());
		double score = rank.buildRankScore();
		cluster.zincrby(key, score, member); 

		// add challenge result data.
		String data = RedisCodecHelper.serialize(rank);
		cluster.hset(rank.buildResultKey(), member, data);
	}
	
	private String buildRankMember(long  playerId) {
		return String.valueOf(playerId);
	}


	public List<CrossRank> queryRank(int rankType, int start, int end) {
		List<CrossRank> ranks = new ArrayList<>();
		Set<Tuple> tupleSet = cluster.zrevrangeWithScores("CrossRank_"  + rankType, start , end );
		
		Class<? extends AbstractCrossRank> rankClazz = rank2Class.get(rankType);
		for (Tuple record:tupleSet) {
			try{
				String element = record.getElement();
				AbstractCrossRank rankProto = rankClazz.newInstance();
				String resultKey = rankProto.buildResultKey();
				String data = cluster.hget(resultKey, element);
				CrossRank rank = unserialize(data, rankClazz);
				ranks.add(rank);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ranks;
	}

	public <T extends CrossRank>  T unserialize(String rankData, Class<T> clazz) {
		return RedisCodecHelper.deserialize(rankData, clazz);
	}

}
