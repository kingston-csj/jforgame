package jforgame.orm.converter.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jforgame.commons.util.StringUtil;
import jforgame.orm.converter.AttributeConversionException;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.io.IOException;

/**
 * Convert object properties to json string for encoding and decoding.
 * If a javabean field type is neither a primitive type nor a String, this converter will be used by default.
 * Unless the field explicitly uses {@link Convert} annotation to specify another converter.
 * Note: When the field modified by this converter is a generic type, such as Map(Key, Object), the Key here cannot be a primitive type, can only be String, because the generic information in the type recorded by jackson is erased.
 * If the modified property is a normal javabean object, the internal javabean can use generics, and key can be a primitive type. But it is still recommended to use String, because the json standard, key itself is a string.
 */
public class ObjectToJsonJpaConverter implements AttributeConverter<Object, String> {

    protected static TypeFactory typeFactory = TypeFactory.defaultInstance();

    protected static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        // Write complete type information for class and property here, so that deserialization can parse even when using Object.class
        MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new AttributeConversionException(e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (StringUtil.isEmpty(dbData)) {
            return null;
        }
        try {
            JavaType type = typeFactory.constructType(Object.class);
            return MAPPER.readValue(dbData, type);
        } catch (IOException e) {
            throw new AttributeConversionException(e);
        }
    }

}