package jforgame.data;

import jforgame.data.annotation.Index;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

interface IndexMeta {

    String getName();

    Object getValue(Object obj);
}

class FieldIndexMeta implements IndexMeta {

    private final Field field;

    private String name;

    FieldIndexMeta(Field field) {
        Index index = field.getAnnotation(Index.class);
        this.field = field;
        this.field.setAccessible(true);
        if (!org.springframework.util.StringUtils.isEmpty(index.name())) {
            name = index.name();
        } else {
            name = field.getName();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue(Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException(obj.getClass().getName() + "无法访问" + field.getName() + "字段");
        }
    }
}

class MethodIndexMeta implements IndexMeta {

    private final Method method;

    private String name;

    MethodIndexMeta(Index index, Method method) {
        this.method = method;
        this.method.setAccessible(true);
        if (!StringUtils.isEmpty(index.name())) {
            name = index.name();
        } else {
            name = method.getName();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue(Object obj) {
        try {
            return method.invoke(obj);
        } catch (Exception e) {
            throw new IllegalStateException(obj.getClass().getName() + "无法访问" + method.getName() + "方法");
        }
    }
}
