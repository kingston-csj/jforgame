package jforgame.orm.converter.support;

import com.fasterxml.jackson.databind.JavaType;
import jforgame.commons.StringUtil;
import jforgame.commons.ZipUtil;
import jforgame.orm.converter.AttributeConversionException;

import java.io.IOException;

/**
 * 将对象属性转换为json字符串并进行压缩编码
 */
public class ObjectToJsonZipJpaConverter extends ObjectToJsonJpaConverter {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return ZipUtil.compressString(MAPPER.writeValueAsString(attribute));
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
            return MAPPER.readValue(ZipUtil.decompressString(dbData), type);
        } catch (IOException e) {
            throw new AttributeConversionException(e);
        }
    }

}