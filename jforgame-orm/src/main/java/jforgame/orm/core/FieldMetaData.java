package jforgame.orm.core;

import jforgame.orm.converter.ConverterFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.lang.reflect.Field;

/**
 * 字段元数据
 */
public class FieldMetaData {

    /**
     * 字段反射对象
     */
    private Field field;

    /**
     * 字段转化器，在读取后，写入前，进行转化
     */
    private AttributeConverter converter;

    public static FieldMetaData valueOf(Field field) {
        field.setAccessible(true);
        FieldMetaData metadata = new FieldMetaData();
        metadata.field = field;
        Convert annotation = field.getAnnotation(Convert.class);
        if (annotation != null) {
            AttributeConverter convert = ConverterFactory.getAttributeConverter(annotation.converter());
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
