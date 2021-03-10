package jforgame.server.cross.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jforgame.server.cross.core.client.CCSession;
import jforgame.server.cross.core.server.SCSession;
import jforgame.common.thread.NamedThreadFactory;

/**
 * 跨服业务执行器
 *
 */
public class CrossCmdExecutor {
	
	private static volatile CrossCmdExecutor self;
	
	private final int DEFAULT_CORE_SUM = Runtime.getRuntime().availableProcessors() - 2;
	
	private ExecutorService[] services;
	
	
	public static CrossCmdExecutor getInstance() {
		if (self != null) {
			return self;
		}
		synchronized (CrossCmdExecutor.class) {
			if (self == null) {
				CrossCmdExecutor instance = new CrossCmdExecutor();
				instance.init();
				self = instance;
			}
		}
		return self;
	}
	
	private void init() {
		services = new ExecutorService[DEFAULT_CORE_SUM];
		for (int i = 0; i < DEFAULT_CORE_SUM; i++) {
			services[i] = Executors.newSingleThreadExecutor(new NamedThreadFactory("cross-service-" + i));
		}
	}
	
	public void addTask(SCSession session, Runnable task) {
		int sessionId = session.getId();
		int index = sessionId % services.length;
		services[index].submit(task);
	}
	
	public void addTask(CCSession session, Runnable task) {
		int sessionId = session.getId();
		int index = sessionId % services.length;
		services[index].submit(task);
	}
	
	public void shutDown() {
		for (int i = 0; i < DEFAULT_CORE_SUM; i++) {
			services[i].shutdown();
		}
	}

}
