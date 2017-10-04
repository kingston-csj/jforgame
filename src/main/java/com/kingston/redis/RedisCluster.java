package com.kingston.redis;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

public final class RedisCluster {

	private static final Map<String, RedisCluster> clusters = new HashMap<>();

	private JedisCluster cluster;

	private RedisCluster(JedisCluster cluster) {
		this.cluster = cluster;
	}

	public static void loadAll() {
		//TODO read config from database
		Map<String, String> config = new HashMap<>();
		config.put("redis_master1", "127.0.0.1:8001");
		config.put("redis_master2", "127.0.0.1:8002");
		config.put("redis_master3", "127.0.0.1:8003");

		config.forEach(RedisCluster::init);
	}

	private static void init(String name, String url) {
		HashSet<HostAndPort> hostAndPorts = new HashSet<>();
		String[] hostPort = url.split(":");
		HostAndPort hostAndPort = new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1]));
		hostAndPorts.add(hostAndPort);
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(50);
		poolConfig.setMinIdle(1);
		poolConfig.setMaxIdle(10);
		JedisCluster cluster = new JedisCluster(hostAndPorts, 2000, poolConfig);
		clusters.put(name, new RedisCluster(cluster));
	}


	public static RedisCluster getRedisCluster(String name) {
		return clusters.get(name);
	}

	public static void destory() {
		clusters.forEach((k, v) -> v.close());
	}

	private void close() {
		try {
			cluster.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public  Set<Tuple> zrangeby(String key, int start, int end) {
		return cluster.zrangeByScoreWithScores(key, start, end);
	}


}
