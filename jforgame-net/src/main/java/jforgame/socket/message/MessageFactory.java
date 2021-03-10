package jforgame.socket.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jforgame.common.utils.ClassScanner;
import jforgame.socket.annotation.MessageMeta;

public enum MessageFactory {

	/** 枚举单例 */
	INSTANCE;

	private Map<Integer, Class<?>> id2Clazz = new HashMap<>();

	private Map<Class<?>, Integer> clazz2Id = new HashMap<>();

	/**
	 * scan all message class and register into pool
	 */
	public void initMessagePool(String scanPath) {
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
			clazz2Id.put(clazz, key);
		}
	}


	public Class<?> getMessage(short module, byte cmd) {
		return id2Clazz.get(buildKey(module, cmd));
	}

	public Class<?> getMessage(int id) {
		short module = (short)(id / 1000);
		byte cmd = (byte)(id % 1000);
		return id2Clazz.get(buildKey(module, cmd));
	}

	public int getMessageId(Class<?> clazz) {
		return clazz2Id.get(clazz);
	}

	private int buildKey(short module, byte cmd) {
		int result = Math.abs(module) * 1000 + Math.abs(cmd);
		return cmd < 0 ? -result : result;
	}

}
