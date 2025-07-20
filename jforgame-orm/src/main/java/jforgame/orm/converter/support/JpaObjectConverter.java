package jforgame.orm.converter.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jforgame.commons.StringUtil;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class JpaObjectConverter implements AttributeConverter<Object, String> {

    protected static TypeFactory typeFactory = TypeFactory.defaultInstance();

    protected static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        // 这里要写入类和属性的完整类型信息，反序列化即使使用Object.class也能解析
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }

}