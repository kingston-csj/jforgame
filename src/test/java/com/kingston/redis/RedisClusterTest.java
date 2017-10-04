package com.kingston.redis;

import org.junit.Test;

public class RedisClusterTest {
	
	@Test
	public void teset() {
		RedisCluster.loadAll();
		
		RedisCluster cluster = RedisCluster.getRedisCluster("redis_master1");
		
		System.err.print(cluster.zrangeby("rank", 0, 100).size());
	}

}
