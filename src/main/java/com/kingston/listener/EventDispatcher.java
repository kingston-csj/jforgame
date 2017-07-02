package com.kingston.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventDispatcher {

	private static EventDispatcher instance = new EventDispatcher();

	private EventDispatcher() {};  

	public static EventDispatcher getInstance() {
		return instance;
	}

	private final Map<EventType, Set<EventListener>> observers = new HashMap<>();  

	/**
	 * 注册事件监听器
	 * @param evtType
	 * @param listener
	 */
	public void registerEvent(EventType evtType, EventListener listener) {  
		Set<EventListener> listeners = observers.get(evtType);  
		if(listeners == null){  
			listeners = new CopyOnWriteArraySet<EventListener>();  
			observers.put(evtType, listeners);  
		}  
		listeners.add(listener);  
	}  

	/**
	 * 分发事件
	 * @param event
	 */
	public void fireEvent(GameEvent event) {  
		if(event == null){  
			throw new NullPointerException("event cannot be null");  
		}  

		EventType evtType = event.getEventType();  
		Set<EventListener> listeners = observers.get(evtType);  
		if(listeners != null){  
			for(EventListener listener:listeners){  
				try{  
					listener.onEvent(event);  
				}catch(Exception e){  
					e.printStackTrace();  //防止其中一个listener报异常而中断其他逻辑  
				}  
			}  
		}  
	}  

}
