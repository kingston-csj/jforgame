package jforgame.orm.converter;

import javax.persistence.AttributeConverter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Converter factory
 * Used to create and cache converter instances
 */
public class ConverterFactory {

    /**
     * Converter cache
     */
    private static final Map<Class<?>, AttributeConverter<Object, Object>> converters = new ConcurrentHashMap<>();

    public static AttributeConverter<Object, Object> getAttributeConverter(Class<?> clazz) {
        return converters.computeIfAbsent(clazz, c -> {
            try {
                return (AttributeConverter) c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
