package com.kingston.jforgame.server.game.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.kingston.jforgame.common.thread.NamedThreadFactory;


public class SchedulerManager {
	
	private static SchedulerManager instance;
	
	private ScheduledExecutorService service;
	
	
	public static SchedulerManager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (SchedulerManager.class) {
			if (instance == null) {
				instance = new SchedulerManager();
				instance.init();
			}
		}
		
		return instance;
	}
	
	private void init() {
		service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(),
				new NamedThreadFactory("common-scheduler"));
	}
	
	/**
	 * 
	 * @param command
	 * @param initialDelay 毫秒数
	 * @param period 毫秒数
	 * @return
	 */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
            long initialDelay, long period) {
    	return service.scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }
	

}
