package jforgame.demo.socket;

import jforgame.codec.struct.StructMessageCodec;
import jforgame.commons.ClassScanner;
import jforgame.commons.TimeUtil;
import jforgame.demo.ServerConfig;
import jforgame.demo.ServerScanPaths;
import jforgame.demo.ServerVersion;
import jforgame.demo.db.BaseEntity;
import jforgame.demo.db.DbService;
import jforgame.demo.db.DbUtils;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.admin.http.HttpCommandManager;
import jforgame.demo.game.admin.http.HttpServer;
import jforgame.demo.game.core.CronSchedulerHelper;
import jforgame.demo.game.core.SystemParameters;
import jforgame.demo.game.database.config.ConfigDataPool;
import jforgame.demo.listener.ListenerManager;
import jforgame.demo.monitor.jmx.GameMonitor;
import jforgame.demo.monitor.jmx.GameMonitorMBean;
import jforgame.demo.redis.RedisCluster;
import jforgame.orm.OrmProcessor;
import jforgame.orm.ddl.SchemaUpdate;
import jforgame.socket.mina.support.server.TcpSocketServerBuilder;
import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.ServerNode;
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

//		if (config.getCrossPort() > 0) {
//			// 启动跨服服务
//			crossServer = NSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getCrossPort()))
//					.setMessageFactory(GameMessageFactory.getInstance())
//					.setMessageCodec(new StructMessageCodec())
//					.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
//					.build();
//
//			crossServer.start();
//		}
		socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
				.setMessageFactory(GameMessageFactory.getInstance())
				.setMessageCodec(new StructMessageCodec())
				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
				.build();

//		// 启动socket服务
//		socketServer = TcpSocketServerBuilder.newBuilder().bindingPort(HostAndPort.valueOf(ServerConfig.getInstance().getServerPort()))
//				.setMessageFactory(GameMessageFactory.getInstance())
//				.setMessageCodec(new StructMessageCodec())
//				.setSocketIoDispatcher(new MessageIoDispatcher(ServerScanPaths.MESSAGE_PATH))
//								.build();

		socketServer.start();
		// 启动http服务
		httpServer = new HttpServer();
		httpServer.start();
	}

	private void loadSystemRecords() throws Exception {
		SystemParameters.load();
		// 启动时检查每日重置
		long now = System.currentTimeMillis();
		if (now - SystemParameters.dailyResetTimestamp > TimeUtil.MILLIS_PER_DAY) {
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
		try {
			socketServer.shutdown();
			httpServer.shutdown();
			if (crossServer != null) {
				crossServer.shutdown();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		DbService.getInstance().shutDown();
		stopWatch.stop();
		logger.error("游戏服关闭成功，耗时[{}]毫秒", stopWatch.getTime());
	}

}
