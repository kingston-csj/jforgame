package com.kingston;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.db.DbService;
import com.kingston.game.core.SchedulerHelper;
import com.kingston.game.core.SystemParameters;
import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.http.HttpServer;
import com.kingston.monitor.jmx.Controller;
import com.kingston.monitor.jmx.ControllerMBean;
import com.kingston.net.MessageFactory;
import com.kingston.net.SocketServer;
import com.kingston.net.context.TaskHandlerContext;
import com.kingston.orm.OrmProcessor;
import com.kingston.orm.utils.DbUtils;
import com.kingston.utils.TimeUtils;

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
		ControllerMBean controller = new Controller();
		mbs.registerMBean(controller, new ObjectName("GameMBean:name=controller"));

	}

	private void frameworkInit() throws Exception {
		//加载服务版本号
		ServerVersion.load();
		//初始化协议池
		MessageFactory.INSTANCE.initMeesagePool();
		//读取服务器配置
		ServerConfig.getInstance().initFromConfigFile();
		//初始化orm框架
		OrmProcessor.INSTANCE.initOrmBridges();
		//初始化消息工作线程池
		TaskHandlerContext.INSTANCE.initialize();
		//初始化数据库连接池
		DbUtils.init();
		//初始化job定时任务
		SchedulerHelper.initAndStart();
		//读取所有策划配置
		ConfigDatasPool.getInstance().loadAllConfigs();
		//异步持久化服务
		DbService.getInstance().init();
		//读取系统参数
		loadSystemRecords();

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
		if (now - SystemParameters.dailyResetTimestamp > 24 * TimeUtils.ONE_HOUR) {
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
