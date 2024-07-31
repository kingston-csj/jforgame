package jforgame.data;

import java.lang.reflect.Field;

interface IdMeta {

    String getName();

    Object getValue(Object obj);

}


class FieldIdMeta implements IdMeta {

    private final Field field;

    private String name;

    FieldIdMeta(Field field) {
        this.field = field;
        this.field.setAccessible(true);
        name = field.getName();
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