package jforgame.orm.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
