package jforgame.commons.eventbus;

import java.lang.reflect.Method;

/**
 * 具体事件订阅者
 */
class Subscriber {

    /**
     * 事件处理者
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
