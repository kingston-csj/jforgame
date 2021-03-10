package jforgame.server.cross.core.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jforgame.server.ServerConfig;
import jforgame.server.game.database.config.ConfigDataPool;
import jforgame.server.game.database.config.bean.ConfigCross;
import jforgame.server.game.database.config.storage.ConfigCrossStorage;
import jforgame.server.cross.core.server.BaseCrossMessageDispatcher;
import jforgame.server.cross.core.server.CMessageDispatcher;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import jforgame.server.logs.LoggerUtils;


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
				config.setTestOnBorrow(true);
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
			return pool.borrowObject();
		} catch (Exception e) {
			LoggerUtils.error("", e);
			return null;
		}
	}


	public CCSession borrowCrossSession() {
		ConfigCrossStorage storage = ConfigDataPool.getInstance().getStorage(ConfigCrossStorage.class);
		ServerConfig serverConfig = ServerConfig.getInstance();
		// 先拿到本服对应的跨服服务器id
		ConfigCross selfServerCross = storage.getConfigCrossBy(serverConfig.getServerId());
		int crossSeverId = selfServerCross.getCrossServer();
		// 再拿到跨服服务器的ip和端口
		ConfigCross targetServerCross = storage.getConfigCrossBy(crossSeverId);
		String ip = targetServerCross.getIp();
		int port = targetServerCross.getRpcPort();
		return borrowSession(ip, port);
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
		this.dispatcher = BaseCrossMessageDispatcher.getInstance();
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
	public boolean validateObject(PooledObject<CCSession> p) {
		return !p.getObject().isExpired();
	}

	@Override
	public PooledObject<CCSession> wrap(CCSession obj) {
		return new DefaultPooledObject<CCSession>(obj);
	}

}
