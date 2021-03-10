package jforgame.orm.ddl;

import jforgame.orm.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ColumnDefinition {

    private static Map<Class<?>, String> java2jdbc = new HashMap<>();

    static {
        java2jdbc.put(Byte.class, "tinyint");
        java2jdbc.put(Byte.TYPE, "tinyint");
        java2jdbc.put(Short.class, "smallint");
        java2jdbc.put(Short.TYPE, "smallint");
        java2jdbc.put(Integer.class, "int");
        java2jdbc.put(Integer.TYPE, "int");
        java2jdbc.put(Float.class, "float");
        java2jdbc.put(Float.TYPE, "float");
        java2jdbc.put(Double.class, "double");
        java2jdbc.put(Double.TYPE, "double");
        java2jdbc.put(Long.class, "bigint");
        java2jdbc.put(Long.TYPE, "bigint");
        // 字符串，默认按最大的，避免溢出
        java2jdbc.put(String.class, "longtext");
    }

    private boolean primary;

    private String name;

    private String defaultValue;

    private String jdbcType;

    private boolean nullable;
    /**
     * 字段注释
     */
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(Class<?> clazz, String jdbcType) {
        if (StringUtils.isNotEmpty(jdbcType)) {
            this.jdbcType = jdbcType;
        } else {
            if (java2jdbc.containsKey(clazz)) {
                this.jdbcType = java2jdbc.get(clazz);
            } else {
                this.jdbcType = java2jdbc.get(String.class);
            }
        }
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
