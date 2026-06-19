package jforgame.data.common;

import jforgame.data.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Injects constants from the configuration table into fields annotated with {@link CommonConfig} in beans
 */
public class CommonValueAutoInjectHandler implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(CommonValueAutoInjectHandler.class);

    private DataManager dataManager;

    private ConversionService conversionService;

    private Map<Class<?>, ConfigValueParser> parserTable = new HashMap<>();

    // Auto inject all ConfigValueParser type beans in Spring container (client custom)
    @Autowired(required = false)
    private List<ConfigValueParser> customParsers;

    public CommonValueAutoInjectHandler(DataManager dataManager, ConversionService conversionService) {
        this.dataManager = dataManager;
        this.conversionService = conversionService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        parserTable.put(NullInjectParser.class, new NullInjectParser());
        parserTable.put(IntArrayConfigValueParser.class, new IntArrayConfigValueParser());
        if (customParsers != null && !customParsers.isEmpty()) {
            for (ConfigValueParser parser : customParsers) {
                Class<? extends ConfigValueParser> parserClass = parser.getClass();
                parserTable.put(parserClass, parser);
                log.info("Registered custom parser: {}", parserClass.getSimpleName());
            }
        }
    }

    void tryInject(Object bean) {
        if (needInject(bean)) {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                CommonConfig annotation = field.getAnnotation(CommonConfig.class);
                if (annotation != null) {
                    ReflectionUtils.makeAccessible(field);

                    String fieldName = StringUtils.isEmpty(annotation.value()) ? field.getName() : annotation.value();
                    CommonContainer commonContainer = dataManager.queryContainer(CommonData.class, CommonContainer.class);
                    CommonData commonDataValue = commonContainer.getConfigValueByKey(fieldName);
                    if (commonDataValue == null) {
                        throw new IllegalStateException(bean.getClass().getSimpleName() + " commonValue is empty, key =" + annotation.value());
                    }
                    Object property = commonDataValue.getValue();
                    if (annotation.parser() != NullInjectParser.class) {
                        ConfigValueParser parser = parserTable.get(annotation.parser());
                        property = parser.convert((String) property);
                        try {
                            field.set(bean, property);
                        } catch (IllegalAccessException ignore) {
                            //
                        }
                    } else {
                        Object fileValue = conversionService.convert(commonDataValue.getValue(), field.getType());
                        try {
                            field.set(bean, fileValue);
                        } catch (IllegalAccessException ignore) {
                            //
                        }
                    }
                }
            }
        }
    }

    private boolean needInject(Object bean) {
        AtomicBoolean result = new AtomicBoolean(false);
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.getAnnotation(CommonConfig.class) != null) {
                result.set(true);
            }
        });

        return result.get();
    }
}
