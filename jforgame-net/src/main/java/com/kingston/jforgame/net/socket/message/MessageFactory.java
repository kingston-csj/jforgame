package com.kingston.jforgame.net.socket.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.jforgame.common.utils.ClassScanner;
import com.kingston.jforgame.net.socket.annotation.MessageMeta;

public enum MessageFactory {

	INSTANCE;

	private Map<Integer, Class<?>> id2Clazz = new HashMap<>();

	private Map<Class<?>, Integer> clazz2Id = new HashMap<>();

	/**
	 * scan all message class and register into pool
	 */
	public void initMeesagePool(String scanPath) {
		Set<Class<?>> messages = ClassScanner.listAllSubclasses(scanPath, Message.class);
		for (Class<?> clazz: messages) {
			MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
			if (meta == null) {
				throw new RuntimeException("messages["+clazz.getSimpleName()+"] missed MessageMeta annotation");
			}
			int key = buildKey(meta.module() , meta.cmd());
			if (id2Clazz.containsKey(key)) {
				throw new RuntimeException("message meta ["+key+"] duplicate！！");
			}
			id2Clazz.put(key,clazz);
		}
	}


	public Class<?> getMessage(short module, short cmd) {
		return id2Clazz.get(buildKey(module, cmd));
	}

	public Class<?> getMessage(int id) {
		short module = (short)(id / 1000);
		short cmd = (short)(id % 1000);
		return id2Clazz.get(buildKey(module, cmd));
	}

	public int getMessageId(Class<?> clazz) {
		return clazz2Id.get(clazz);
	}

	private int buildKey(short module, short cmd) {
		return module * 1000 +  + cmd;
	}

}
