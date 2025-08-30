package jforgame.orm.core;

import jforgame.commons.util.TypeUtil;
import jforgame.orm.converter.ConverterFactory;
import jforgame.orm.converter.support.ObjectToJsonJpaConverter;

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

        // 不是基本类型， 或者字符串，自动转换
        if (!TypeUtil.isPrimitiveOrString(metadata.getField().getType())) {
            AttributeConverter convert = ConverterFactory.getAttributeConverter(ObjectToJsonJpaConverter.class);
            Convert annotation = field.getAnnotation(Convert.class);
            if (annotation != null) {
                convert = ConverterFactory.getAttributeConverter(annotation.converter());
            }
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
