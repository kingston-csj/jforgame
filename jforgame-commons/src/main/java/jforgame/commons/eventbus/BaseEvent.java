package jforgame.commons.eventbus;

/**
 * 事件基类
 */
public interface BaseEvent {

    default Object getOwner() {
        return "system";
    }

}
