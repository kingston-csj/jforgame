package jforgame.commons.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON utility based on Jackson
 * Note: During deserialization, undeclared fields will be ignored
 */
public final class JsonUtil {


    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Converts an object to a JSON string.
     * Returns null if the object is null or serialization fails.
     */
    public static String object2String(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonParseException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Parses a JSON string into an object of the specified type.
     * Throws JsonParseException if the JSON is invalid.
     */
    public static <T> T string2Object(String json, Class<T> clazz) {
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        JavaType type = typeFactory.constructType(clazz);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse JSON to " + clazz.getName()
                    + ", json=" + truncate(json, 200), e);
        }
    }

    /**
     * Converts a map to a JSON string.
     * Throws JsonParseException if serialization fails.
     */
    public static String map2String(Map<?, ?> map) {
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new JsonParseException("Failed to serialize map to JSON", e);
        }
    }

    /**
     * Parses a JSON string into a Map<String, Object>.
     * Throws JsonParseException if the JSON is invalid.
     */
    public static Map<String, Object> string2Map(String json) {
        JavaType type = typeFactory.constructMapType(HashMap.class, String.class, Object.class);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse JSON to Map<String,Object>, json="
                    + truncate(json, 200), e);
        }
    }

    /**
     * Parses a JSON string into a Map with specified key and value types.
     * Throws JsonParseException if the JSON is invalid.
     */
    public static <K, V> Map<K, V> string2Map(String json, Class<K> keyClazz, Class<V> valueClazz) {
        JavaType type = typeFactory.constructMapType(HashMap.class, keyClazz, valueClazz);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse JSON to Map<"
                    + keyClazz.getSimpleName() + "," + valueClazz.getSimpleName() + ">, json="
                    + truncate(json, 200), e);
        }
    }

    /**
     * Parses a JSON string into an array of the specified element type.
     * Throws JsonParseException if the JSON is invalid.
     */
    public static <T> T[] string2Array(String json, Class<T> clazz) {
        ArrayType type = typeFactory.constructArrayType(clazz);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse JSON to " + clazz.getSimpleName() + "[]", e);
        }
    }

    /**
     * Parses a JSON string into a collection of the specified element type.
     * Throws JsonParseException if the JSON is invalid.
     */
    public static <C extends Collection<E>, E> C string2Collection(String json,
                                                                   Class<C> collectionType,
                                                                   Class<E> elemType) {
        JavaType type = typeFactory.constructCollectionType(collectionType, elemType);
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse JSON to "
                    + collectionType.getSimpleName() + "<" + elemType.getSimpleName() + ">", e);
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null) {
            return null;
        }
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    /**
     * Runtime exception thrown when JSON parsing fails.
     */
    public static class JsonParseException extends RuntimeException {

        public JsonParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

