package jforgame.data.convertor;

import jforgame.commons.JsonUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JsonToListConvertor implements ConditionalGenericConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() == String.class && List.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, List.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String json = (String) source;
        if (targetType.getElementTypeDescriptor().getType().isPrimitive()) {
            return JsonUtil.string2Object(json, targetType.getObjectType());
        }
        Object[] array = JsonUtil.string2Array(json, targetType.getElementTypeDescriptor().getType());
        if (array != null) {
            List<Object> list = new ArrayList<>(array.length);
            list.addAll(Arrays.asList(array));
            return list;
        }
        return null;
    }
}
