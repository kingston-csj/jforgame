package jforgame.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程工厂
 * @author kinson
 */
public class NamedThreadFactory implements ThreadFactory {

	private ThreadGroup threadGroup;

	private String groupName;

	private final boolean daemo;

	private AtomicInteger idGenerator = new AtomicInteger(1);

	public NamedThreadFactory(String group) {
		this(group, false);
	}

	public NamedThreadFactory(String group, boolean daemo) {
		this.groupName = group;
		this.daemo = daemo;
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = getNextThreadName();
		Thread ret = new Thread(threadGroup, r, name, 0);
		ret.setDaemon(daemo);
		return ret;
	}

	private String getNextThreadName() {
		return this.groupName + "-thread-" + this.idGenerator.getAndIncrement();
	}

}
