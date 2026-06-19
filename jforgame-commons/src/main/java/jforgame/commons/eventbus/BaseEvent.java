package jforgame.commons.eventbus;

/**
 * Base event class
 */
public interface BaseEvent {

    default Object getOwner() {
        return "system";
    }

}
