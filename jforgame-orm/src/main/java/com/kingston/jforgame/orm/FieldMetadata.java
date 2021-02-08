package com.kingston.jforgame.orm;

import com.kingston.jforgame.orm.converter.AttributeConverter;
import com.kingston.jforgame.orm.converter.Convert;
import com.kingston.jforgame.orm.converter.ConvertorUtil;

import java.lang.reflect.Field;

public class FieldMetadata {

    private Field field;

    private AttributeConverter converter;

    public static FieldMetadata valueOf(Field field) {
        field.setAccessible(true);
        FieldMetadata metadata = new FieldMetadata();
        metadata.field = field;
        Convert annotation = field.getAnnotation(Convert.class);
        if (annotation != null) {
            AttributeConverter convert = ConvertorUtil.getAttributeConverter(annotation.converter());
            metadata.converter = convert;
        }
        return metadata;
    }

    public Field getField() {
        return field;
    }

    public AttributeConverter getConverter() {
        return converter;
    }
}
