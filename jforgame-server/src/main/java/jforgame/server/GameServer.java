package jforgame.server;

import jforgame.server.cross.core.CrossServer;
import jforgame.server.db.BaseEntity;
import jforgame.server.db.DbService;
import jforgame.server.db.DbUtils;
import jforgame.server.game.GameContext;
import jforgame.server.game.admin.http.HttpCommandManager;
import jforgame.server.game.admin.http.HttpServer;
import jforgame.server.game.core.CronSchedulerHelper;
import jforgame.server.game.core.SystemParameters;
import jforgame.server.game.database.config.ConfigDataPool;
import jforgame.server.listener.ListenerManager;
import jforgame.server.monitor.jmx.GameMonitor;
import jforgame.server.monitor.jmx.GameMonitorMBean;
import jforgame.server.net.mina.MinaSocketServer;
import jforgame.server.redis.RedisCluster;
import jforgame.common.utils.ClassScanner;
import jforgame.common.utils.TimeUtil;
import jforgame.orm.OrmProcessor;
import jforgame.orm.ddl.SchemaUpdate;
import jforgame.orm.utils.DbHelper;
import jforgame.socket.ServerNode;
import jforgame.socket.message.MessageFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

public class GameServer {

	private static Logger logger = LoggerFactory.getLogger(GameServer.class);

	private static GameServer gameServer = new GameServer();

	private ServerNode socketServer;

	private ServerNode httpServer;

	private ServerNode crossServer;

	public static GameServer getInstance() {
		return gameServer;
	}

	public void start() throws Exception {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// 游戏基础框架服务启动
		frameworkInit();
		// 游戏业务初始化
		gameLogicInit();

		stopWatch.stop();
		logger.error("游戏服启动成功，耗时[{}]毫秒", stopWatch.getTime());

		// mbean监控
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		GameMonitorMBean controller = new GameMonitor();
		mbs.registerMBean(controller, new ObjectName("GameMXBean:name=GameMonitor"));
	}

	private void frameworkInit() throws Exception {

		GameContext.init();
		// 加载服务版本号
		ServerVersion.load();
		// 初始化协议池
		MessageFactory.INSTANCE.initMessagePool(ServerScanPaths.MESSAGE_PATH);
		// 读取服务器配置
		ServerConfig config = ServerConfig.getInstance();
		// 初始化orm框架
		OrmProcessor.INSTANCE.initOrmBridges(ServerScanPaths.ORM_PATH);
		// 初始化数据库连接池
		DbUtils.init();

		// 数据库自动更新schema
		Set<Class<?>> codeTables = ClassScanner.listAllSubclasses("jforgame.server.game.database.user", BaseEntity.class);
		new SchemaUpdate().execute(DbUtils.getConnection(DbUtils.DB_USER), codeTables);

		// 事件驱动
		ListenerManager.INSTANCE.init();
		// 初始化job定时任务
		CronSchedulerHelper.initAndStart();
		// 读取所有策划配置
		ConfigDataPool.getInstance().loadAllConfigs();
		// 异步持久化服务
		DbService.getInstance().init();
		// 读取系统参数
		loadSystemRecords();
		// Redis cache
		RedisCluster.INSTANCE.init();
		// http admin commands
		HttpCommandManager.getInstance().initialize(ServerScanPaths.HTTP_ADMIN_PATH);

		GameContext.gmManager.init();

		if (config.getCrossPort() > 0) {
			// 启动跨服服务
			crossServer = new CrossServer();
			crossServer.start();
		}
		// 启动socket服务
		socketServer = new MinaSocketServer();
//		socketServer = new NettySocketServer(config.getMaxReceiveBytes());
		socketServer.start();
		// 启动http服务
		httpServer = new HttpServer();
		httpServer.start();
	}

	private void loadSystemRecords() throws Exception {
		SystemParameters.load();
		// 启动时检查每日重置
		long now = System.currentTimeMillis();
		if (now - SystemParameters.dailyResetTimestamp > TimeUtil.ONE_DAY) {
			logger.info("启动时每日重置");
			SystemParameters.update("dailyResetTimestamp", now);
		}
	}

	private void gameLogicInit() {
		// 游戏启动时，各种业务初始化写在这里吧
		GameContext.playerManager.loadAllPlayerProfiles();
		// 跨服天梯
//		LadderFightManager.getInstance().init();
	}

	public void shutdown() {
		logger.error("游戏进程准备关闭");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// 各种业务逻辑的关闭写在这里。。。
		socketServer.shutdown();
		httpServer.shutdown();
		if (crossServer != null) {
			crossServer.shutdown();
		}
		DbService.getInstance().shutDown();
		stopWatch.stop();
		logger.error("游戏服关闭成功，耗时[{}]毫秒", stopWatch.getTime());
	}

}
