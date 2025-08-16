package jforgame.orm.core;

import jforgame.commons.StringUtil;
import jforgame.orm.converter.AttributeConversionException;
import jforgame.orm.entity.StatefulEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL参数工具类
 * 用于获取参数化查询的参数值
 */
class SqlParameterUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlParameterUtils.class);

    /**
     * 获取插入SQL的参数值
     */
    public static List<Object> getInsertParameters(StatefulEntity entity, OrmBridge bridge) {
        List<String> properties = bridge.listAllProperties();
        return getFieldValues(entity, properties, bridge);
    }

    /**
     * 获取更新SQL的参数值
     */
    public static List<Object> getUpdateParameters(StatefulEntity entity, OrmBridge bridge) {
        Set<String> columns = entity.getAllModifiedColumns();
        boolean saveAll = entity.isSaveAll();

        List<Object> updateValues = new ArrayList<>();
        List<Object> whereValues = new ArrayList<>();

        // 获取SET子句的参数
        for (Map.Entry<String, FieldMetaData> entry : bridge.getFieldMetadataMap().entrySet()) {
            String property = entry.getKey();
            if (!saveAll && !columns.contains(property)) {
                continue;
            }

            try {
                FieldMetaData metadata = entry.getValue();
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }
                updateValues.add(value);
            } catch (Exception e) {
                logger.error("Failed to get field value for property: {}", property, e);
                throw new AttributeConversionException(e);
            }
        }

        // 获取WHERE子句的参数
        for (String property : bridge.getPrimaryKeyProperties()) {
            try {
                Object value = getMethodValue(entity, property);
                whereValues.add(value);
            } catch (Exception e) {
                logger.error("Failed to get query property value: {}", property, e);
                throw new RuntimeException(e);
            }
        }

        // 合并参数：先SET参数，后WHERE参数
        List<Object> allParameters = new ArrayList<>(updateValues);
        allParameters.addAll(whereValues);

        return allParameters;
    }

    /**
     * 获取删除SQL的参数值
     */
    public static List<Object> getDeleteParameters(StatefulEntity entity, OrmBridge bridge) {
        List<Object> whereValues = new ArrayList<>();
        for (String property : bridge.getPrimaryKeyProperties()) {
            try {
                Object value = getMethodValue(entity, property);
                whereValues.add(value);
            } catch (Exception e) {
                logger.error("Failed to get query property value: {}", property, e);
                throw new RuntimeException("Failed to get query property value: " + property, e);
            }
        }

        return whereValues;
    }

    /**
     * 获取字段值列表
     */
    private static List<Object> getFieldValues(StatefulEntity entity, List<String> properties, OrmBridge bridge) {
        List<Object> values = new ArrayList<>();
        for (String property : properties) {
            try {
                FieldMetaData metadata = bridge.getFieldMetadataMap().get(property);
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }
                values.add(value);
            } catch (Exception e) {
                logger.error("Failed to get field value for property: {}", property, e);
                values.add(null);
            }
        }
        return values;
    }

    private static Object getMethodValue(Object obj, String property)
            throws Exception {
        String methodName = "get" + StringUtil.firstLetterToUpperCase(property);
        Method method = obj.getClass().getMethod(methodName);
        return method.invoke(obj);
    }

} 