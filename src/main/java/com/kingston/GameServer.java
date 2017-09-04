package com.kingston;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.db.DbService;
import com.kingston.game.database.config.ConfigDatasPool;
import com.kingston.game.http.HttpServer;
import com.kingston.logs.LoggerUtils;
import com.kingston.monitor.jmx.Controller;
import com.kingston.monitor.jmx.ControllerMBean;
import com.kingston.net.MessageFactory;
import com.kingston.net.SocketServer;
import com.kingston.net.context.TaskHandlerContext;
import com.kingston.orm.OrmProcessor;
import com.kingston.orm.utils.DbUtils;

public class GameServer {

	private static Logger logger = LoggerFactory.getLogger(GameServer.class);

	private static GameServer gameServer = new GameServer();

	private SocketServer socketServer;

	public static GameServer getInstance() {
		return gameServer;
	}

	public void start() {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		//游戏框架服务启动
		frameworkStart();
		//游戏业务初始化
		gameLogicStart();

		stopWatch.stop();
		logger.error("游戏服务启动，耗时[{}]毫秒", stopWatch.getTime());

		//mbean监控
		try{
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();  
			ControllerMBean controller = new Controller();    
			//将MBean注册到MBeanServer中    
			mbs.registerMBean(controller, new ObjectName("GameMBean:name=controller")); 
		}catch(Exception e){
			LoggerUtils.error("register mbean failed ", e);
		}

	}

	private void frameworkStart() {
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
		//读取所有策划配置
		ConfigDatasPool.getInstance().loadAllConfigs();
		//异步持久化服务
		DbService.getInstance().init();

		try{
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();  
			//创建MBean    
			ControllerMBean controller = new Controller();    
			//将MBean注册到MBeanServer中    
			mbs.registerMBean(controller, new ObjectName("GameMBean:name=controller")); 
		}catch(Exception e){
			LoggerUtils.error("register mbean failed ", e);
		}

		//启动socket服务
		try{
			socketServer = new SocketServer();
			socketServer.start();
		}catch(Exception e) {
			LoggerUtils.error("ServerStarter failed ", e);
		}
		//启动http服务
		try{
			new HttpServer().start();
		}catch(Exception e) {
			LoggerUtils.error("HttpServer failed ", e);
		}
	}


	private void gameLogicStart() {

	}


	public void shutdown() {
		logger.error("游戏进程准备关闭");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		//各种业务逻辑的关闭写在这里。。。
		stopWatch.stop();
		logger.error("游戏服务正常关闭，耗时[{}]毫秒", stopWatch.getTime());
	}

}
