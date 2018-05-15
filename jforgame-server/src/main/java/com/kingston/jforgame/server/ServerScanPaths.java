package com.kingston.jforgame.server;

/**
 * 各种配置记取路径申明
 * @author kingston
 */
public class ServerScanPaths {

	/**
	 * 需要进行orm映射的玩家动态数据以及配置静态数据
	 */
	public static final String ORM_PATH = "com.kingston.jforgame.server.game";

	/**
	 * io通信消息
	 */
	public static final String MESSAGE_PATH = "com.kingston.jforgame";

	/**
	 * 后台http命令
	 */
	public static final String HTTP_ADMIN_PATH = "com.kingston.jforgame.server.game.admin.commands";
}
