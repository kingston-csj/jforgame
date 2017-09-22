package com.kingston.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.net.annotation.MessageMeta;
import com.kingston.utils.ClassScanner;

public enum MessageFactory {

	INSTANCE;

	private Map<String, Class<?>> messagePool = new HashMap<>();

	private final String SCAN_PATH = "com.kingston";

	/**
	 * scan all message class and register into pool
	 */
	public void initMeesagePool() {
		Set<Class<?>> messages = ClassScanner.listAllSubclasses(SCAN_PATH, Message.class);

		for (Class<?> clazz: messages) {
			MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
			if (meta == null) {
				throw new RuntimeException("messages["+clazz.getSimpleName()+"] missed MessageMeta annotation");
			}
			String key = meta.module() + "_" + meta.cmd();
			if (messagePool.containsKey(key)) {
				throw new RuntimeException("message meta ["+key+"] duplicate！！");
			}
			messagePool.put(key,clazz);
		}
	}


	public Class<?> getMessage(short module, short cmd) {
		return messagePool.get(buildKey(module, cmd));
	}

	private String buildKey(short module, short cmd) {
		return module + "_" + cmd;
	}

}
