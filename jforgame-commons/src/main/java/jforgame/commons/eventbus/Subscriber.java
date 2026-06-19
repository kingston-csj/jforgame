package jforgame.commons.eventbus;

import java.lang.reflect.Method;

/**
 * Specific event subscriber
 */
class Subscriber {

    /**
     * Event handler
     */
    private Object listener;

    private Method method;

    public Subscriber(Object listener, Method method) {
        super();
        this.listener = listener;
        this.method = method;
    }

    public void handleEvent(BaseEvent event) {
        try {
            method.invoke(listener, event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
