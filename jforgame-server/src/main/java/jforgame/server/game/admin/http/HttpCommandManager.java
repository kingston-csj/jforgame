package jforgame.server.game.admin.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.common.utils.ClassScanner;

/**
 * @author kinson
 */
public class HttpCommandManager {

	private Logger logger = LoggerFactory.getLogger(HttpCommandManager.class);

	private static volatile HttpCommandManager instance = new HttpCommandManager();

	private static Map<Integer, HttpCommandHandler> handlers = new HashMap<>();

	public static HttpCommandManager getInstance() {
		return instance;
	}

	/**
	 * @param cmdPath path which contains all the http commands
	 */
	public void initialize(String cmdPath) {
		Set<Class<?>> handleClazzs = ClassScanner.listClassesWithAnnotation(cmdPath, CommandHandler.class);
		for (Class<?> clazz: handleClazzs) {
			try {
				HttpCommandHandler handler = (HttpCommandHandler) clazz.newInstance();
				CommandHandler annotation = handler.getClass().getAnnotation(CommandHandler.class);
				handlers.put(annotation.cmd(), handler);
			}catch(Exception e) {
				logger.error("", e);
			}
		}
	}


	/**
	 * 处理后台命令
	 * @param httpParams
	 * @return
	 */
	public HttpCommandResponse handleCommand(HttpCommandParams httpParams) {
		HttpCommandHandler handler = handlers.get(httpParams.getCmd());
		if (handler != null) {
			return handler.action(httpParams);
		}
		return null;
	}
}
