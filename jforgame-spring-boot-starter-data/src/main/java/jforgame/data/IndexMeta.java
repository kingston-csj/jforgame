package jforgame.data;

import jforgame.data.annotation.Index;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 索引元信息
 */
interface IndexMeta {

    String getName();

    /**
     * 获取索引值
     * @param obj 配置记录
     * @return 索引值
     */
    Object getValue(Object obj);

    /**
     * 是否唯一索引
     * @return
     */
    boolean isUnique();
}

/**
 * 基于字段的索引元信息
 */
class FieldIndexMeta implements IndexMeta {

    private final Field field;

    private final String name;

    private final boolean unique;

    FieldIndexMeta(Field field) {
        Index index = field.getAnnotation(Index.class);
        this.field = field;
        this.field.setAccessible(true);
        if (!org.springframework.util.StringUtils.isEmpty(index.name())) {
            name = index.name();
        } else {
            name = field.getName();
        }
        this.unique = index.unique();
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
            throw new IllegalStateException(obj.getClass().getName() + "无法访问" + field.getName() + "字段");
        }
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}

/**
 * 基于方法的索引元信息
 */
class MethodIndexMeta implements IndexMeta {

    private final Method method;

    private final String name;
    private final boolean unique;

    MethodIndexMeta(Index index, Method method) {
        this.method = method;
        this.method.setAccessible(true);
        if (!StringUtils.isEmpty(index.name())) {
            name = index.name();
        } else {
            name = method.getName();
        }
        this.unique = index.unique();
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

    @Override
    public boolean isUnique() {
        return unique;
    }
}
