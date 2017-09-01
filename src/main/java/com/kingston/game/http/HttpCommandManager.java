package com.kingston.game.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.logs.LoggerUtils;
import com.kingston.utils.ClassFilter;
import com.kingston.utils.ClassScanner;

public class HttpCommandManager {

	private static volatile HttpCommandManager instance;

	private static Map<Integer, HttpCommandHandler> handlers = new HashMap<>();

	public static HttpCommandManager getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (HttpCommandManager.class) {
			if (instance == null) {
				instance = new HttpCommandManager();
				instance.initialize();
			}
			return instance;
		}
	}

	private void initialize() {
		Set<Class<?>> handleClazzs = ClassScanner.getClasses("com.kingston.http", new ClassFilter() {  
			@Override  
			public boolean accept(Class<?> clazz) {  
				return clazz.getAnnotation(CommandHandler.class) != null;  
			}  
		});  

		for (Class<?> clazz: handleClazzs) {  
			try {  
				HttpCommandHandler handler = (HttpCommandHandler) clazz.newInstance(); 
				CommandHandler annotation = handler.getClass().getAnnotation(CommandHandler.class);
				handlers.put(annotation.cmd(), handler);
			}catch(Exception e) {  
				LoggerUtils.error("", e);
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
