package jforgame.server.listener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jforgame.server.listener.annotation.EventHandler;
import jforgame.server.listener.annotation.Listener;
import jforgame.common.utils.ClassScanner;
import jforgame.server.logs.LoggerUtils;

public enum ListenerManager {

	/** 枚举单例 */
	INSTANCE;

	private Map<String, Method> map = new HashMap<>();

	private final String SCAN_PATH = "jforgame.server";

	public void init() {
        Set<Class<?>> listeners = ClassScanner.listClassesWithAnnotation(SCAN_PATH, Listener.class);

        for (Class<?> listener: listeners) {
            try {
                Object handler = listener.newInstance();
                Method[] methods = listener.getDeclaredMethods();
                for (Method method:methods) {
                	EventHandler mapperAnnotation = method.getAnnotation(EventHandler.class);
                    if (mapperAnnotation != null) {
                    	EventType[] eventTypes = mapperAnnotation.value();
                    	 for(EventType eventType: eventTypes) {
                    		 EventDispatcher.getInstance().registerEvent(eventType, handler);
                    		 map.put(getKey(handler, eventType), method);
                         }
                    }
                }
            }catch(Exception e) {
                LoggerUtils.error("", e);
            }
        }
    }

	/**
	 * 分发给具体监听器执行
	 * @param handler
	 * @param event
	 */
	public void fireEvent(Object handler,BaseGameEvent event) {
		try {
			Method method = map.get(getKey(handler, event.getEventType()));
			method.invoke(handler, event);
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	private String getKey(Object handler, EventType eventType) {
		return handler.getClass().getName() + "-" + eventType.toString();
	}

}
