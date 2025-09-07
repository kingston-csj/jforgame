package jforgame.orm.converter.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jforgame.commons.StringUtil;
import jforgame.orm.converter.AttributeConversionException;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.io.IOException;

/**
 * 将对象属性转换为json字符串进行编解码
 * 如果一个javabean的字段类型既不是基本类型，也不是字符串，默认会使用这个转换器
 * 除非该字段显式使用 {@link Convert} 注解指定了其他的转换器
 * 需要注意的是，当该转换器修饰的字段是一个泛型类型时，例如Map<Key, Object>，这里的Key不能是基本类型，只能是字符串，因为jackson记录的类型里，泛型信息被擦除了
 * 如果修饰的属性是一个普通的javabean对象，这个javabean内部可以使用泛型，key可以是基本类型。但建议还是使用String，因为json标准，key本来就是字符串。
 */
public class ObjectToJsonJpaConverter implements AttributeConverter<Object, String> {

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