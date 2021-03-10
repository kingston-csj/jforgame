package jforgame.server.redis;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisTest {

	Jedis jedis ;

	@Before
	public void init() {
		//连接本地的 Redis 服务
		jedis = new Jedis("localhost");

	}

	@Test
	public void testConnection() {
		System.out.println("connected succ");
		System.out.println("服务正在运行: "+jedis.ping());
	}

	@Test
	public void testSortedSet() {

		String key = "rank";
		jedis.zremrangeByRank(key, 0, 100);
		jedis.zadd("rank", 0, "a");
		jedis.zadd("rank", 0, "b");
		jedis.zadd("rank", 0, "c");
		jedis.zadd("rank", 5, "d");
		jedis.zadd("rank", 2, "e");

		assertTrue(jedis.zcount(key, 0, 100) == 5);
	}

}
