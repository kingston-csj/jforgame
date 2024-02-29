package jforgame.demo.listener;

import jforgame.commons.ClassScanner;
import jforgame.demo.game.logger.LoggerUtils;
import jforgame.demo.listener.annotation.EventHandler;
import jforgame.demo.listener.annotation.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ListenerManager {

	/** 枚举单例 */
	INSTANCE;

	private Map<String, Method> map = new HashMap<>();

	private final String SCAN_PATH = "jforgame.server";

	private Logger logger = LoggerFactory.getLogger(getClass());

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
                logger.error("", e);
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
