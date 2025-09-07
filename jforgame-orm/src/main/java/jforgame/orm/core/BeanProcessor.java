package jforgame.orm.core;

import jforgame.commons.util.TypeUtil;
import jforgame.orm.converter.ConverterFactory;
import jforgame.orm.converter.support.ObjectToJsonJpaConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该工具类拷贝自Apache的DbUtil工具库。
 * 这里增加了若干拓展，支持数据库字符串到java的Enum类的转化
 * 支持引用对象对数据库的转换 {@link AttributeConverter},
 * 如果一个javabean的字段类型既不是基本类型，也不是字符串，默认会使用 {@link ObjectToJsonJpaConverter} 转换器
 * 除非该字段显式使用 {@link Convert} 注解指定了其他的转换器
 */
public class BeanProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BeanProcessor.class);
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<>();
    private final Map<String, String> columnToPropertyOverrides;

    static {
        primitiveDefaults.put(Integer.TYPE, 0);
        primitiveDefaults.put(Short.TYPE, (short) 0);
        primitiveDefaults.put(Byte.TYPE, (byte) 0);
        primitiveDefaults.put(Float.TYPE, 0.0F);
        primitiveDefaults.put(Double.TYPE, 0.0D);
        primitiveDefaults.put(Long.TYPE, 0L);
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, '\0');
    }

    public BeanProcessor() {
        this(new HashMap<>());
    }

    public BeanProcessor(Map<String, String> columnToPropertyOverrides) {
        if (columnToPropertyOverrides == null) {
            throw new IllegalArgumentException("columnToPropertyOverrides map cannot be null");
        }
        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    public <T> T toBean(ResultSet rs, Class<T> type)
            throws SQLException {
        PropertyDescriptor[] props = propertyDescriptors(type);

        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToProperty = mapColumnsToProperties(rsmd, props);

        return createBean(rs, type, props, columnToProperty);
    }

    public <T> List<T> toBeanList(ResultSet rs, Class<T> type)
            throws SQLException {
        List<T> results = new ArrayList<>();
        if (!rs.next()) {
            return results;
        }
        PropertyDescriptor[] props = propertyDescriptors(type);
        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToProperty = mapColumnsToProperties(rsmd, props);
        do {
            results.add(createBean(rs, type, props, columnToProperty));
        } while (rs.next());
        return results;
    }

    private <T> T createBean(ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty)
            throws SQLException {
        T bean = newInstance(type);
        for (int i = 1; i < columnToProperty.length; i++) {
            if (columnToProperty[i] != -1) {
                PropertyDescriptor prop = props[columnToProperty[i]];
                Class<?> propType = prop.getPropertyType();

                Object value = null;
                if (propType != null) {
                    value = processColumn(rs, i, propType);
                    if ((value == null) && (propType.isPrimitive())) {
                        value = primitiveDefaults.get(propType);
                    }
                }
                callSetter(bean, prop, value);
            }
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private void callSetter(Object target, PropertyDescriptor prop, Object value)
            throws SQLException {
        Method setter = prop.getWriteMethod();
        Class<?> clazzType = target.getClass();
        if (setter == null) {
            logger.info("实体[{}]字段[{}]没有对应的setter方法", clazzType.getName(), prop.getName());
            return;
        }
        Class<?>[] params = setter.getParameterTypes();
        try {
            if ((value instanceof java.util.Date)) {
                String targetType = params[0].getName();
                if (java.sql.Date.class.getName().equals(targetType)) {
                    value = new java.sql.Date(((java.util.Date) value).getTime());
                } else if (java.sql.Time.class.getName().equals(targetType)) {
                    value = new Time(((java.util.Date) value).getTime());
                } else if (java.sql.Timestamp.class.getName().equals(targetType)) {
                    Timestamp tsValue = (Timestamp) value;
                    int nanos = tsValue.getNanos();
                    value = new Timestamp(tsValue.getTime());
                    ((Timestamp) value).setNanos(nanos);
                }
            } else if (((value instanceof String))) {
                if (params[0].isEnum()) {
                    @SuppressWarnings("rawtypes")
                    Class c = params[0].asSubclass(Enum.class);
                    value = Enum.valueOf(c, (String) value);
                }
                try {
                    Field field = clazzType.getDeclaredField(prop.getName());
                    // 不是基本类型， 或者字符串，自动转换
                    if (!TypeUtil.isPrimitiveOrString(field.getType())) {
                        AttributeConverter convert = ConverterFactory.getAttributeConverter(ObjectToJsonJpaConverter.class);
                        Convert annotation = field.getAnnotation(Convert.class);
                        if (annotation != null) {
                            convert = ConverterFactory.getAttributeConverter(annotation.converter());
                        }
                        value = convert.convertToEntityAttribute(value);
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
            if (TypeUtil.isCompatibleType(value, params[0])) {
                setter.invoke(target, value);
            } else {
                throw new SQLException("Cannot set " + prop.getName() + ": incompatible types, cannot convert " + value.getClass().getName() + " to " + params[0].getName());
            }
        } catch (Exception e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    private <T> T newInstance(Class<T> c)
            throws SQLException {
        try {
            return c.newInstance();
        } catch (Exception e) {
            throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    private PropertyDescriptor[] propertyDescriptors(Class<?> c)
            throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);
        } catch (IntrospectionException e) {
            throw new SQLException("Bean introspection failed: " + e.getMessage());
        }
        return beanInfo.getPropertyDescriptors();
    }

    protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props)
            throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, -1);
        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if ((null == columnName) || (columnName.isEmpty())) {
                columnName = rsmd.getColumnName(col);
            }
            String propertyName = this.columnToPropertyOverrides.get(columnName);
            if (propertyName == null) {
                propertyName = columnName;
            }
            for (int i = 0; i < props.length; i++) {
                if (propertyName.equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }
        return columnToProperty;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Object processColumn(ResultSet rs, int index, Class<?> propType)
            throws SQLException {
        if ((!propType.isPrimitive()) && (rs.getObject(index) == null)) {
            return null;
        }
        if (propType.equals(String.class) || propType.getAnnotation(Convert.class) != null) {
            return rs.getString(index);
        }
        if ((propType.equals(Integer.TYPE)) || (propType.equals(Integer.class))) {
            return rs.getInt(index);
        }
        if ((propType.equals(Boolean.TYPE)) || (propType.equals(Boolean.class))) {
            return rs.getBoolean(index);
        }
        if ((propType.equals(Long.TYPE)) || (propType.equals(Long.class))) {
            return rs.getLong(index);
        }
        if ((propType.equals(Double.TYPE)) || (propType.equals(Double.class))) {
            return rs.getDouble(index);
        }
        if ((propType.equals(Float.TYPE)) || (propType.equals(Float.class))) {
            return rs.getFloat(index);
        }
        if ((propType.equals(Short.TYPE)) || (propType.equals(Short.class))) {
            return rs.getShort(index);
        }
        if ((propType.equals(Byte.TYPE)) || (propType.equals(Byte.class))) {
            return rs.getByte(index);
        }
        if (propType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);
        }
        if (propType.equals(SQLXML.class)) {
            return rs.getSQLXML(index);
        }
        if (propType.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) propType, rs.getString(index));
        }
        return rs.getObject(index);
    }

}