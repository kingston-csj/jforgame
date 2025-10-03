package jforgame.data.convertor;

import jforgame.commons.util.JsonUtil;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class JsonToMapConvertor implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType() == String.class && Map.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Map.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String json = (String) source;
        return JsonUtil.string2Map(json, targetType.getMapKeyTypeDescriptor().getType(), targetType.getMapValueTypeDescriptor().getType());
    }

}
