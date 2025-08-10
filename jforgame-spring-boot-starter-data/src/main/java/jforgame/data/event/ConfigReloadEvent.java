package jforgame.data.event;

import org.springframework.context.ApplicationEvent;

/**
 * 配置表重载事件
 */
public class ConfigReloadEvent extends ApplicationEvent {

    /**
     * @param source 配置表名称
     */
    public ConfigReloadEvent(Object source) {
        super(source);
    }
}
