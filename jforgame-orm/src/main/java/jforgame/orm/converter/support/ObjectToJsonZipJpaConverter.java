package jforgame.orm.converter.support;

import com.fasterxml.jackson.databind.JavaType;
import jforgame.commons.util.StringUtil;
import jforgame.commons.util.ZipUtil;
import jforgame.orm.converter.AttributeConversionException;

import java.io.IOException;

/**
 * Converts object property to json string and compresses it.
 * For fields with large data volume, such as player backpack data, it is recommended to use this converter,
 * because json compression ratio is very high.
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