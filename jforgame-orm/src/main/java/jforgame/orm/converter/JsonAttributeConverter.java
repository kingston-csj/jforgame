package jforgame.orm.converter;

import jforgame.commons.JsonUtil;
import jforgame.orm.utils.StringUtils;

import java.util.Objects;

public class JsonAttributeConverter implements AttributeConverter<Object, String> {

    /**
     * 将实体转为json字符串
     * 如果对象为空，返回空字符串
     * @param attribute the entity attribute value to be converted
     * @return
     */
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (Objects.isNull(attribute)) {
            return "";
        }
        return JsonUtil.object2String(attribute);
    }

    @Override
    public Object convertToEntityAttribute(Class<Object> clazz, String json) {
        if (StringUtils.isNotEmpty(json)) {
            return JsonUtil.string2Object(json, clazz);
        }
        return null;
    }
}
