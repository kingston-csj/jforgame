package jforgame.socket.support;

import jforgame.common.ClassScanner;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageFactoryImpl implements MessageFactory {

	/**
	 * 枚举单例
	 */
	private static MessageFactoryImpl instance = new MessageFactoryImpl();

	private Map<Integer, Class> id2Clazz = new HashMap<>();

	private Map<Class, Integer> clazz2Id = new HashMap<>();

	public static MessageFactoryImpl getInstance() {
		return instance;
	}

	/**
	 * scan all message class and register into pool
	 */
	public void initMessagePool(String scanPath) {
		Set<Class<?>> messages = ClassScanner.listClassesWithAnnotation(scanPath, MessageMeta.class);
		for (Class<?> clazz : messages) {
			MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
			int key = buildKey(meta.module(), meta.cmd());
			registerMessage(key, clazz);
		}
	}


	@Override
	public void registerMessage(int cmd, Class<?> clazz) {
		if (instance.id2Clazz.containsKey(cmd)) {
			throw new IllegalStateException("message meta [" + cmd + "] duplicate！！");
		}
		MessageMeta meta = clazz.getAnnotation(MessageMeta.class);
		if (meta == null) {
			throw new RuntimeException("messages[" + clazz.getSimpleName() + "] missed MessageMeta annotation");
		}
		Logger logger = LoggerFactory.getLogger(MessageFactoryImpl.class);
		logger.debug("message {} {} ", cmd, clazz.getSimpleName());
		instance.id2Clazz.put(cmd, clazz);
		instance.clazz2Id.put(clazz, cmd);
	}

	@Override
	public Class  getMessage(int cmd) {
		return instance.id2Clazz.get(cmd);
	}


	@Override
	public int getMessageId(Class clazz) {
		return instance.clazz2Id.get(clazz);
	}

	private int buildKey(short module, int cmd) {
		int result = Math.abs(module) * 1000 + Math.abs(cmd);
		return cmd < 0 ? -result : result;
	}

}
