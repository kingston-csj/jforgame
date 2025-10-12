package jforgame.commons.eventbus;

import jforgame.commons.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 事件总线
 * 推荐使用单例模式获取实例
 * @since 2.3.0
 */
public class EventBus {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Executor executor;

    private SubscriberRegistry registry;


    public EventBus () {
        // 异步执行的需求很少，一条线程就够了
        this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("EventBus-Async-Thread"));
        this.registry = new SubscriberRegistry();
    }

    /**
     * 从指定订阅者注册所有的事件监听
     * 该方法会寻找订阅者类中所有被@Subscribe注解标记的方法，并注册到事件总线中
     * @param subscriber 订阅者对象
     */
    public void register(Object subscriber) {
        registry.register(subscriber);
    }

    /**
     * 同步处理事件
     * 该方法会同步执行所有订阅了该事件的监听方法
     * @param event 事件对象
     */
    public void post(BaseEvent event) {
        Class<? extends BaseEvent> eventType = event.getClass();
        Set<Subscriber> subscribers = registry.getSubscribersForEvent(eventType);

        subscribers.forEach((subscriber) -> {
            try {
                subscriber.handleEvent(event);
            } catch (Exception e) {
                logger.error("", e);
            }
        });
    }

    /**
     * 异步处理事件
     *
     * 该方法会异步执行所有订阅了该事件的监听方法
     * @param event 事件对象
     */
    public void asyncPost(BaseEvent event) {
        this.executor.execute(() -> {
            post(event);
        });
    }
}
