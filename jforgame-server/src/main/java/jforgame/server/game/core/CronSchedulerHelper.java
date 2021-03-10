package jforgame.server.game.core;

import java.util.Objects;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * job定时器挂点
 */
public class CronSchedulerHelper {

	private static Scheduler scheduler;

	private final static String CONFIG_PATH = "jobs/quartz.properties";

	public static synchronized void setScheduler(Scheduler scheduler) {
		Objects.requireNonNull(scheduler);
		CronSchedulerHelper.scheduler = scheduler;
	}

	public static synchronized Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * 初始化
	 * @param path 配置文件的路径
	 * @throws SchedulerException
	 */
	public static synchronized void initAndStart(String path) throws SchedulerException {
		Objects.requireNonNull(path);
		SchedulerFactory schedulerFactory;
		schedulerFactory = new StdSchedulerFactory(path);
		scheduler = schedulerFactory.getScheduler();
		scheduler.start();
	}

	public static synchronized void initAndStart() throws SchedulerException {
		SchedulerFactory schedulerFactory;
		schedulerFactory = new StdSchedulerFactory(CONFIG_PATH);
		scheduler = schedulerFactory.getScheduler();
		scheduler.start();
	}

	/**
	 * 初始化
	 * @param path 配置文件的路径
	 * @throws SchedulerException
	 */
	public static synchronized void init(String path) throws SchedulerException {
		Objects.requireNonNull(path);
		SchedulerFactory schedulerFactory = new StdSchedulerFactory(path);
		scheduler = schedulerFactory.getScheduler();
	}


}
