package jforgame.orm.core;

import jforgame.commons.util.StringUtil;
import jforgame.orm.converter.AttributeConversionException;
import jforgame.orm.entity.StatefulEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SQL parameter utility class.
 * Used to get parameter values for parameterized queries.
 */
class SqlParameterUtils {

    private static final Logger logger = LoggerFactory.getLogger(SqlParameterUtils.class);

    /**
     * Get parameter values for insert SQL
     */
    public static List<Object> getInsertParameters(StatefulEntity entity, OrmBridge bridge) {
        List<String> properties = bridge.listAllProperties();
        return getFieldValues(entity, properties, bridge);
    }

    /**
     * Get parameter values for update SQL
     */
    public static List<Object> getUpdateParameters(StatefulEntity entity, OrmBridge bridge) {
        Set<String> columns = entity.getAllModifiedColumns();
        boolean saveAll = entity.isSaveAll();

        List<Object> updateValues = new ArrayList<>();
        List<Object> whereValues = new ArrayList<>();

        // Get parameters for SET clause
        for (String property : bridge.listAllProperties()) {
            if (!saveAll && !columns.contains(property)) {
                continue;
            }

            try {
                FieldMetaData metadata = bridge.getFieldMetadataMap().get(property);
                if (metadata == null) {
                    throw new IllegalStateException("FieldMetaData is null, property: " + property);
                }
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

        // Get parameters for   WHERE clause
        for (String property : bridge.getPrimaryKeyProperties()) {
            try {
                Object value = getMethodValue(entity, property);
                whereValues.add(value);
            } catch (Exception e) {
                logger.error("Failed to get query property value: {}", property, e);
                throw new RuntimeException(e);
            }
        }

        // Merge parameters: SET parameters first, then WHERE parameters
        List<Object> allParameters = new ArrayList<>(updateValues);
        allParameters.addAll(whereValues);

        return allParameters;
    }

    /**
     * Get parameter values for delete SQL
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
     * Get field values list
     */
    private static List<Object> getFieldValues(StatefulEntity entity, List<String> properties, OrmBridge bridge) {
        List<Object> values = new ArrayList<>();
        for (String property : properties) {
            try {
                FieldMetaData metadata = bridge.getFieldMetadataMap().get(property);
                if (metadata == null) {
                    throw new IllegalStateException("FieldMetaData is null, property: " + property);
                }
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }
                values.add(value);
            } catch (Exception e) {
                logger.error("Failed to get field value for property: {}", property, e);
                throw new AttributeConversionException(e);
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
