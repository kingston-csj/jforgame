package jforgame.server.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.server.logs.LoggerUtils;

public class EventDispatcher {

	private static EventDispatcher instance = new EventDispatcher();

	private EventDispatcher() {
		new NamedThreadFactory("event-dispatch").newThread(new EventWorker()).start();
	};

	public static EventDispatcher getInstance() {
		return instance;
	}

	/** 事件类型与事件监听器列表的映射关系 */
	private final Map<EventType, Set<Object>> observers = new HashMap<>();
	/** 异步执行的事件队列 */
	private LinkedBlockingQueue<BaseGameEvent> eventQueue = new LinkedBlockingQueue<>();

	/**
	 * 注册事件监听器
	 * @param evtType
	 * @param listener
	 */
	public void registerEvent(EventType evtType, Object listener) {
		Set<Object> listeners = observers.get(evtType);
		if(listeners == null){
			listeners = new CopyOnWriteArraySet<>();
			observers.put(evtType, listeners);
		}
		listeners.add(listener);
	}

	/**
	 * 分发事件
	 * @param event
	 */
	public void fireEvent(BaseGameEvent event) {
		if(event == null){
			throw new NullPointerException("event cannot be null");
		}
		//如果事件是同步的，那么就在消息主线程执行逻辑
		if (event.isSynchronized()) {
			triggerEvent(event);
		} else {
		//否则，就丢到事件线程异步执行
			eventQueue.add(event);
		}
	}

	private void triggerEvent(BaseGameEvent event) {
		EventType evtType = event.getEventType();
		Set<Object> listeners = observers.get(evtType);
		if(listeners != null){
			listeners.forEach(listener->{
				try{
					ListenerManager.INSTANCE.fireEvent(listener, event);
				}catch(Exception e){
					//防止其中一个listener报异常而中断其他逻辑
					LoggerUtils.error("triggerEvent failed", e);
				}
			});
		}
	}

	private class EventWorker implements Runnable {
		@Override
		public void run() {
			while(true) {
				try {
					BaseGameEvent event = eventQueue.take();
					triggerEvent(event);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
