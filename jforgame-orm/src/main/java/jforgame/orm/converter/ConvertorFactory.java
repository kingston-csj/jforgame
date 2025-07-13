package jforgame.orm.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转换器工厂
 * 用于创始并缓存转换器实例
 */
public class ConvertorFactory {

    private static final Map<Class<?>, AttributeConverter<Object, Object>> converters = new ConcurrentHashMap<>();

    public static AttributeConverter<Object, Object> getAttributeConverter(Class<?> clazz) {
        return converters.computeIfAbsent(clazz, c -> {
            try {
                return (AttributeConverter) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
