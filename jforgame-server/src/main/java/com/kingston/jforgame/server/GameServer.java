package com.kingston.jforgame.server;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.common.utils.TimeUtil;
import com.kingston.jforgame.net.http.HttpCommandManager;
import com.kingston.jforgame.net.http.HttpServer;
import com.kingston.jforgame.net.socket.SocketServer;
import com.kingston.jforgame.net.socket.message.MessageFactory;
import com.kingston.jforgame.net.socket.task.TaskHandlerContext;
import com.kingston.jforgame.orm.OrmProcessor;
import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.core.SchedulerHelper;
import com.kingston.jforgame.server.game.core.SystemParameters;
import com.kingston.jforgame.server.game.database.config.ConfigDatasPool;
import com.kingston.jforgame.server.monitor.jmx.GameMonitor;
import com.kingston.jforgame.server.monitor.jmx.GameMonitorMXBean;
import com.kingston.jforgame.server.redis.RedisCluster;

public class GameServer {

	private static Logger logger = LoggerFactory.getLogger(GameServer.class);

	private static GameServer gameServer = new GameServer();

	private SocketServer socketServer;

	private HttpServer httpServer;

	public static GameServer getInstance() {
		return gameServer;
	}

	public void start() throws Exception {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		//游戏基础框架服务启动
		frameworkInit();
		//游戏业务初始化
		gameLogicInit();

		stopWatch.stop();
		logger.error("游戏服务启动，耗时[{}]毫秒", stopWatch.getTime());

		//mbean监控
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		GameMonitorMXBean controller = new GameMonitor();
		mbs.registerMBean(controller, new ObjectName("GameMXBean:name=GameMonitor"));

	}

	private void frameworkInit() throws Exception {
		//加载服务版本号
		ServerVersion.load();
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool(ServerScanPaths.MESSAGE_PATH);
		//读取服务器配置
		ServerConfig.getInstance().initFromConfigFile();
		//初始化orm框架
		OrmProcessor.INSTANCE.initOrmBridges(ServerScanPaths.ORM_PATH);
		//初始化数据库连接池
		DbUtils.init();
		//初始化消息工作线程池
		TaskHandlerContext.INSTANCE.initialize();
		//初始化job定时任务
		SchedulerHelper.initAndStart();
		//读取所有策划配置
		ConfigDatasPool.getInstance().loadAllConfigs();
		//异步持久化服务
		DbService.getInstance().init();
		//读取系统参数
		loadSystemRecords();
		//Redis cache
		RedisCluster.INSTANCE.init();
		//http admin commands
		HttpCommandManager.getInstance().initialize(ServerScanPaths.HTTP_ADMIN_PATH);
		//启动socket服务
		socketServer = new SocketServer();
		socketServer.start();
		//启动http服务
		httpServer = new HttpServer();
		httpServer.start();
	}

	private void loadSystemRecords() throws Exception {
		SystemParameters.load();
		// 启动时检查每日重置
		long now = System.currentTimeMillis();
		if (now - SystemParameters.dailyResetTimestamp > 24 * TimeUtil.ONE_HOUR) {
			logger.info("启动时每日重置");
			SystemParameters.update("dailyResetTimestamp", now);
		}
	}


	private void gameLogicInit() {
		//游戏启动时，各种业务初始化写在这里吧
	}


	public void shutdown() {
		logger.error("游戏进程准备关闭");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		//各种业务逻辑的关闭写在这里。。。
		socketServer.shutdown();
		httpServer.shutdown();

		stopWatch.stop();
		logger.error("游戏服务正常关闭，耗时[{}]毫秒", stopWatch.getTime());
	}

}
