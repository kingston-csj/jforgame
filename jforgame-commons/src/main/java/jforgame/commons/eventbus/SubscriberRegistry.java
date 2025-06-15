package jforgame.commons.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 订阅者注册表
 */
class SubscriberRegistry {

    private final ConcurrentMap<Class<? extends BaseEvent>, CopyOnWriteArraySet<Subscriber>> subscribers = new ConcurrentHashMap<>();

    void register(Object subscriber) {
        Map<Class<? extends BaseEvent>, Subscriber> listenerMethods = findAllSubscribers(subscriber);

        for (Map.Entry<Class<? extends BaseEvent>, Subscriber> entry : listenerMethods.entrySet()) {
            Class<? extends BaseEvent> eventType = entry.getKey();

            subscribers.putIfAbsent(eventType, new CopyOnWriteArraySet<>());
            CopyOnWriteArraySet<Subscriber> eventSubscribers = subscribers.get(eventType);

            eventSubscribers.add(entry.getValue());
        }
    }

    private Map<Class<? extends BaseEvent>, Subscriber> findAllSubscribers(Object listener) {
        Map<Class<? extends BaseEvent>, Subscriber> methodsInListener = new HashMap<>();
        Class<?> clazz = listener.getClass();
        for (Method method : getAnnotatedMethods(clazz)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            @SuppressWarnings("unchecked")
            Class<? extends BaseEvent> eventType = (Class<? extends BaseEvent>) parameterTypes[0];
            methodsInListener.put(eventType, new Subscriber(listener, method));
        }
        return methodsInListener;
    }

    private static List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException(
                            "Method " + method.getName() + " has @Subscribe annotation but has" + parameterTypes.length
                                    + " parameters." + "Subscriber methods must have exactly 1 parameter.");

                }
                result.add(method);
            }
        }
        return result;
    }

    public Set<Subscriber> getSubscribersForEvent(Class<? extends BaseEvent> eventType) {
        if (!subscribers.containsKey(eventType)) {
            return Collections.emptySet();
        }
        return subscribers.get(eventType);
    }

}
