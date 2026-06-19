package jforgame.data.common;

import jforgame.data.ResourceOptions;
import jforgame.data.event.ConfigReloadEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Common constant configuration registry. Iterates through all Spring beans annotated with {@link org.springframework.stereotype.Service},
 * (Note: beans annotated with other {@link org.springframework.stereotype.Component} are NOT processed)
 * Injects common constant configuration {@link CommonData} into fields annotated with {@link CommonConfig} in beans.
 * The {@link CommonConfig#value()} must match {@link CommonData#getKey()} as the same string.
 * The field value will be converted according to the {@link ConfigValueParser} property first, then by {@link org.springframework.core.convert.ConversionService}.
 * If the common constant table is hot-updated during runtime, publish event {@link ConfigReloadEvent} via {@link ApplicationEventPublisher} with the configuration table name as parameter.
 */
@Order
public class ConfigResourceRegistry implements ApplicationContextAware, ApplicationListener<ConfigReloadEvent> {

    @Autowired
    private CommonValueAutoInjectHandler systemValueAutoInjectHandler;

    @Autowired
    ResourceOptions resourceOptions;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        autoInjectStaticConfig();
    }

    private void autoInjectStaticConfig() {
        Map<String, Object> services = applicationContext.getBeansWithAnnotation(Service.class);
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            systemValueAutoInjectHandler.tryInject(entry.getValue());
        }
        // Reload all CommonValueReloadListener implementations
        for (Map.Entry<String, CommonValueReloadListener> entry : applicationContext.getBeansOfType(CommonValueReloadListener.class).entrySet()) {
            entry.getValue().afterReload();
        }
    }

    /**
     * Hot-reload configuration table during service runtime. Publish event via {@link ApplicationEventPublisher#publishEvent(Object)}.
     * The event is {@link ConfigReloadEvent} with configuration table name as parameter.
     */
    @Override
    public void onApplicationEvent(ConfigReloadEvent event) {
        // If it's the common constant table, re-inject static configuration secondary cache
        if (resourceOptions.getCommonTableName().equalsIgnoreCase(event.getSource().toString())) {
            autoInjectStaticConfig();
        }
    }
}
