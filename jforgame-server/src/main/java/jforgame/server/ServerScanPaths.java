package jforgame.server;

/**
 * 各种配置记取路径申明
 * 
 * @author kinson
 */
public class ServerScanPaths {

	/**
	 * 需要进行orm映射的玩家动态数据以及配置静态数据
	 */
	public static final String ORM_PATH = "jforgame.server.game";

	/**
	 * rpc回调处理器
	 */
	public static final String RPC_CALL_BACK_PATH = "jforgame.server";

	/**
	 * io通信消息
	 */
	public static final String MESSAGE_PATH = "jforgame.server";

	/**
	 * 后台http命令
	 */
	public static final String HTTP_ADMIN_PATH = "jforgame.server.game.admin.commands";
}
