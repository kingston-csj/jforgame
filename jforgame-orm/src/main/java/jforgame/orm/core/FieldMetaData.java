package jforgame.orm.core;

import jforgame.commons.util.TypeUtil;
import jforgame.orm.converter.ConverterFactory;
import jforgame.orm.converter.support.ObjectToJsonJpaConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.lang.reflect.Field;

/**
 * Field metadata
 */
public class FieldMetaData {

    /**
     * Field reflection object
     */
    private Field field;

    /**
     * Field converter, used for conversion after reading and before writing
     */
    private AttributeConverter converter;

    public static FieldMetaData valueOf(Field field) {
        field.setAccessible(true);
        FieldMetaData metadata = new FieldMetaData();
        metadata.field = field;

        // If not primitive type or String, auto convert
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
