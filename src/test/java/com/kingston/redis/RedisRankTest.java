package com.kingston.redis;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.kingston.game.crossrank.CrossRank;
import com.kingston.game.crossrank.CrossRankKinds;
import com.kingston.game.crossrank.CrossRankService;
import com.kingston.game.crossrank.impl.CrossLevelRank;

public class RedisRankTest {
	
	@Test
	public void test() {
		RedisCluster cluster = RedisCluster.INSTANCE;
		cluster.init();
		cluster.clearAllData();
		CrossRankService rankService = CrossRankService.getInstance();
		
		final int N_RECORD =  10;
		for (int i=1;i<N_RECORD*2;i++) {
			rankService.addRank(new CrossLevelRank(i, 100+i));
		}
		
		List<CrossRank> ranks = rankService.queryRank(CrossRankKinds.FIGHTING, 1, N_RECORD);
		for (CrossRank rank:ranks) {
			System.err.println(rank);
		}
		assertTrue(ranks.size() == N_RECORD);
		assertTrue(ranks.get(0).getScore() >= ranks.get(1).getScore());
		
	}
	
}
