package jforgame.demo.db;

import jforgame.commons.JsonUtil;
import jforgame.orm.converter.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class JsonAttributeConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return JsonUtil.object2String(attribute);
    }

    @Override
    public Object convertToEntityAttribute(Class<Object> clazz, String dbData) {
        if (StringUtils.isNoneEmpty(dbData)) {
            return JsonUtil.string2Object(dbData, clazz);
        }
        return null;
    }
}
