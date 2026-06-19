package jforgame.data.event;

import org.springframework.context.ApplicationEvent;

/**
 * Configuration reload event
 */
public class ConfigReloadEvent extends ApplicationEvent {

    /**
     * @param source the configuration table name
     */
    public ConfigReloadEvent(Object source) {
        super(source);
    }
}
