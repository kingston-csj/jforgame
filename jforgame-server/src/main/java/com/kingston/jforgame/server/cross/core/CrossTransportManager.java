package com.kingston.jforgame.server.cross.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.kingston.jforgame.server.cross.core.callback.CallbackTask;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import com.kingston.jforgame.server.cross.core.client.C2SSessionPoolFactory;
import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.socket.message.Message;

public class CrossTransportManager {

	private static volatile CrossTransportManager instance;

	private int defaultCoreSum = Runtime.getRuntime().availableProcessors();

	private ExecutorService[] services;

	private ExecutorService asynService;


	private C2SSessionPoolFactory sessionFactory;
	
	public static CrossTransportManager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (CrossTransportManager.class) {
			if (instance == null) {
				CrossTransportManager obj = new CrossTransportManager();
				obj.init();
				instance = obj;
			}
			
		}
		return instance;
	}

	private void init() {
		services = new ExecutorService[defaultCoreSum];
		for (int i = 0; i < defaultCoreSum; i++) {
			services[i] = Executors.newSingleThreadExecutor(new NamedThreadFactory("cross-ladder-transport" + i));
		}

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxTotal(5);
		config.setMaxWaitMillis(5000);
		sessionFactory = new C2SSessionPoolFactory(config);

		asynService = Executors.newFixedThreadPool(defaultCoreSum);
	}

	/**
	 * 同步发消息
	 * 
	 * @param ip
	 * @param port
	 * @param message
	 */
	public void sendMessageSync(String ip, int port, Message message) {
		CCSession session = sessionFactory.borrowSession(ip, port);
		session.sendMessage(message);
	}

	/**
	 * 异步发消息
	 * 
	 * @param ip
	 * @param port
	 * @param message
	 */
	public void sendMessageAsync(String ip, int port, Message message) {
		String key = (ip + port).toString();
		int index = key.hashCode() % defaultCoreSum;
		services[index].submit(() -> {
			sendMessageSync(ip, port, message);
		});
		CCSession session = sessionFactory.borrowSession(ip, port);
		session.sendMessage(message);
	}

	public Message callBack(CCSession session, Message message) throws ExecutionException, InterruptedException {
		CallbackTask task = CallbackTask.valueOf(session, message);
		return asynService.submit(task).get();
	}


}
