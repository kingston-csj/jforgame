package jforgame.data.convertor;

import jforgame.commons.util.JsonUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Set;

public class JsonToArrayConvertor implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() == String.class && targetType.getType().isArray();
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Object[].class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String json = (String) source;
        if (targetType.getElementTypeDescriptor().getType().isPrimitive()) {
            return JsonUtil.string2Object(json, targetType.getObjectType());
        }
        return JsonUtil.string2Array(json, targetType.getElementTypeDescriptor().getType());
    }

}
