package jforgame.server.db;

import jforgame.server.utils.JsonUtils;
import jforgame.orm.converter.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class JsonAttributeConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return JsonUtils.object2String(attribute);
    }

    @Override
    public Object convertToEntityAttribute(Class<Object> clazz, String dbData) {
        if (StringUtils.isNoneEmpty(dbData)) {
            return JsonUtils.string2Object(dbData, clazz);
        }
        return null;
    }
}
