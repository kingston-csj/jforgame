package jforgame.data.common;

import jforgame.data.ResourceProperties;
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
 * 通用常量配置仓库, 遍历所有带{@link org.springframework.stereotype.Service}注解的spring bean，
 * (注意：其他{@link org.springframework.stereotype.Component}注解的bean<br>不</br>处理)
 * 将通用常量配置{@link CommonData}注入到bean中带{@link CommonConfig}注解的字段
 * 其中，{@link CommonConfig#value()}并且与{@link CommonData#getKey()}为相同的字符串
 * 该字段会优化按{@link ConfigValueParser}属性进行转换，然后按{@link org.springframework.core.convert.ConversionService}进行转换
 * 若在程序运行期间，对通用常量表进行了热更新，则需要通过{@link ApplicationEventPublisher}发布事件{@link ConfigReloadEvent}，参数为配置表名称，
 */
@Order
public class ConfigResourceRegistry implements ApplicationContextAware, ApplicationListener<ConfigReloadEvent> {

    @Autowired
    private CommonValueAutoInjectHandler systemValueAutoInjectHandler;

    @Autowired
    ResourceProperties resourceProperties;

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
        // 对 CommonValueReloadListener实现类进行reload
        for (Map.Entry<String, CommonValueReloadListener> entry : applicationContext.getBeansOfType(CommonValueReloadListener.class).entrySet()) {
            entry.getValue().afterReload();
        }
    }

    /**
     * 服务运行期间，热更配置表，可通过 {@link ApplicationEventPublisher#publishEvent(Object)}发布事件
     * 事件为 {@link ConfigReloadEvent}, 参数为配置表名称
     */
    @Override
    public void onApplicationEvent(ConfigReloadEvent event) {
        // 如果是通用常量表，则重新注入静态配置二级缓存
        if (resourceProperties.getCommonTableName().equalsIgnoreCase(event.getSource().toString())) {
            autoInjectStaticConfig();
        }
    }
}
