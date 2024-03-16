package jforgame.commons.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 */
public class NamedThreadFactory implements ThreadFactory {

	private ThreadGroup threadGroup;

	private String groupName;

	private final boolean daemon;

	private AtomicInteger idGenerator = new AtomicInteger(1);

	public NamedThreadFactory(String group) {
		this(group, false);
	}

	public NamedThreadFactory(String group, boolean daemon) {
		this.groupName = group;
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = getNextThreadName();
		Thread ret = new Thread(threadGroup, r, name, 0);
		ret.setDaemon(daemon);
		return ret;
	}

	private String getNextThreadName() {
		return this.groupName + "-thread-" + this.idGenerator.getAndIncrement();
	}

}
