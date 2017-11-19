package com.kingston.jforgame.server.redis;

import org.junit.Test;

import com.kingston.jforgame.server.redis.RedisCluster;

public class RedisClusterTest {
	
	@Test
	public void test() {
		
		RedisCluster cluster = RedisCluster.INSTANCE;
		
		cluster.init();
		
		System.err.print(cluster.zrangeWithScores("rank", 0, 100).size());
	}

}
