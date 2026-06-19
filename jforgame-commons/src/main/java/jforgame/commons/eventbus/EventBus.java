package jforgame.commons.eventbus;

import jforgame.commons.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Event bus
 * Recommended to use singleton pattern to get instance
 * @since 2.3.0
 */
public class EventBus {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Executor executor;

    private SubscriberRegistry registry;


    public EventBus() {
        // Async execution needs are few, one thread is enough
        this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("EventBus-Async-Thread"));
        this.registry = new SubscriberRegistry();
    }

    /**
     * Register all event listeners from specified subscriber
     * This method will find all methods marked with @Subscribe annotation in subscriber class, and register to event bus
     * @param subscriber subscriber object
     */
    public void register(Object subscriber) {
        registry.register(subscriber);
    }

    /**
     * Synchronously process event
     * This method will synchronously execute all listener methods subscribed to this event
     * Note: If a subclass event is published, all listener methods listening to its parent class event will be executed
     * For example: Publish PlayerLoginEvent, then listener methods listening to PlayerEvent (PlayerLoginEvent parent class) will also be executed
     * @param event event object
     */
    public void post(BaseEvent event) {
        for (Class<?> clazz = event.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            @SuppressWarnings("unchecked")
            Set<Subscriber> subscribers = registry.getSubscribersForEvent((Class<? extends BaseEvent>) clazz);
            subscribers.forEach((subscriber) -> {
                try {
                    subscriber.handleEvent(event);
                } catch (Exception e) {
                    logger.error("", e);
                }
            });
        }
    }

    /**
     * Asynchronously process event
     *
     * This method will asynchronously execute all listener methods subscribed to this event
     * @param event event object
     */
    public void asyncPost(BaseEvent event) {
        this.executor.execute(() -> {
            post(event);
        });
    }
}
