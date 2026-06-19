package jforgame.data;

import jforgame.data.annotation.Index;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Index metadata
 */
interface IndexMeta {

    String getName();

    /**
     * Gets index value
     * @param obj configuration record
     * @return index value
     */
    Object getValue(Object obj);

    /**
     * Whether it is a unique index
     * @return true if unique
     */
    boolean isUnique();
}

/**
 * Field-based index metadata
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
            throw new IllegalStateException(obj.getClass().getName() + "cannot access field " + field.getName());
        }
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}

/**
 * Method-based index metadata
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
            throw new IllegalStateException(obj.getClass().getName() + "cannot access method " + method.getName());
        }
    }

    @Override
    public boolean isUnique() {
        return unique;
    }
}
