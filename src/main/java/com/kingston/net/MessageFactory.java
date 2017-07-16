package com.kingston.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.net.annotation.Protocol;
import com.kingston.utils.ClassFilter;
import com.kingston.utils.ClassScanner;

public enum MessageFactory {
	
	INSTANCE;
	
	private Map<String, Class<?>> messagePool = new HashMap<>();
	
	/**
	 * 初始化所有通信协议库
	 */
	public void initMeesagePool() {
		Set<Class<?>> messages = ClassScanner.getClasses("com.kingston.game", new ClassFilter() {
			@Override
			public boolean accept(Class<?> clazz) {
				return Message.class.isAssignableFrom(clazz);
			}
		});
		
		for (Class<?> clazz: messages) {
			Protocol protocol = clazz.getAnnotation(Protocol.class);
			if (protocol == null) {
				throw new RuntimeException("messages["+clazz.getSimpleName()+"]缺少Protocol注解");
			}
			String key = protocol.module() + "_" + protocol.cmd();
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
