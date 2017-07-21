package com.kingston.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 * @author kingston
 */
public class NameableThreadFactory implements ThreadFactory {
	
	private ThreadGroup threadGroup;
	
	private String groupName;
	
	private AtomicInteger idGenerator = new AtomicInteger(1);
	
	public NameableThreadFactory(String group) {
		this.groupName = group;
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(threadGroup, r, getNextThreadName());
	}
	
	public Thread newDaemonThread(Runnable r) {
		Thread t =  new Thread(threadGroup, r, getNextThreadName());
		t.setDaemon(true);
		return t;
	}
	
	private String getNextThreadName() {
		return this.groupName + "-thread-" + this.idGenerator.getAndIncrement();
	}

}
