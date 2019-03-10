package com.kingston.jforgame.server.cross.core.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.jforgame.common.utils.ClassScanner;

public class MatchMessageFactory {

	private volatile static MatchMessageFactory instance;

	private Map<String, Class<?>> signature2Clazz = new HashMap<>();

	public static MatchMessageFactory getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (MatchMessageFactory.class) {
			if (instance == null) {
				instance = new MatchMessageFactory();
				instance.initialize();
			}
		}
		return instance;
	}

	public void initialize() {
		Set<Class<?>> messages = ClassScanner.listAllSubclasses("com.kingston.jforgame.server",
				AbstractMatchMessage.class);

		for (Class<?> controller : messages) {
			try {
				signature2Clazz.put(controller.getSimpleName(), controller);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Class<?> getMessageBy(String signature) {
		return signature2Clazz.get(signature);
	}

}
