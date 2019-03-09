package com.kingston.jforgame.server.cross.core.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kingston.jforgame.server.cross.core.server.BaseCMessageDispatcher;
import com.kingston.jforgame.server.cross.core.server.CMessageDispatcher;


public class C2SSessionPoolFactory {

	private GenericObjectPoolConfig config;
	
	private ConcurrentMap<String, GenericObjectPool<CCSession>> pools = new ConcurrentHashMap<>();
	
	private static volatile C2SSessionPoolFactory self;
	
	public static C2SSessionPoolFactory getInstance() {
		if (self != null) {
			return self;
		}
		synchronized (C2SSessionPoolFactory.class) {
			if (self == null) {
				GenericObjectPoolConfig config = new GenericObjectPoolConfig();
				config.setMaxTotal(Runtime.getRuntime().availableProcessors());
				C2SSessionPoolFactory instance = new C2SSessionPoolFactory(config);
				self = instance;
			}
		}
		return self;
	}

	public C2SSessionPoolFactory(GenericObjectPoolConfig config) {
		this.config = config;
	}

	public CCSession borrowSession(String ip, int port) {
		String key = buildKey(ip, port);
		try {
			C2SSessionFactory factory = new C2SSessionFactory(ip, port);
			GenericObjectPool<CCSession> pool = pools.getOrDefault(key, new GenericObjectPool(factory, config));
			pools.putIfAbsent(key, pool);
			return (CCSession) pool.borrowObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void returnSession(CCSession session) {
		String key = buildKey(session.getIpAddr(), session.getPort());
		GenericObjectPool<CCSession> pool = pools.get(key);
		if (pool != null) {
			pool.returnObject(session);
		}
	}
	
	private String buildKey(String ip, int port) {
		return  ip + "-" + port;
	}
}

class C2SSessionFactory extends BasePooledObjectFactory<CCSession> {

	String ip;

	int port;

	CMessageDispatcher dispatcher;

	public C2SSessionFactory(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		this.dispatcher = BaseCMessageDispatcher.getInstance();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public CCSession create() throws Exception {
		CCSession session = CCSession.valueOf(ip, port, dispatcher);
		session.buildConnection();
		return session;
	}

	@Override
	public PooledObject<CCSession> wrap(CCSession obj) {
		return new DefaultPooledObject<CCSession>(obj);
	}

}
