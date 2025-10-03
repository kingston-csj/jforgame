package jforgame.orm.converter.support;

import com.fasterxml.jackson.databind.JavaType;
import jforgame.commons.util.StringUtil;
import jforgame.commons.util.ZipUtil;
import jforgame.orm.converter.AttributeConversionException;

import java.io.IOException;

/**
 * 将对象属性转换为json字符串并进行压缩编码
 * 对一些字段数据比较大的，例如玩家背包数据，推荐使用这个转换器，因为json的压缩率是非常高的。
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