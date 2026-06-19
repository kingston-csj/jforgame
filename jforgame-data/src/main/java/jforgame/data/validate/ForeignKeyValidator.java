package jforgame.data.validate;

import jforgame.data.Container;
import jforgame.data.DataRepository;
import jforgame.data.annotation.DataTable;
import jforgame.data.annotation.ForeignKey;
import jforgame.data.exception.DataValidateException;
import jforgame.data.exception.ForeignKeyConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Checks all foreign key constraints for a single table
 */
public class ForeignKeyValidator implements DataValidator {

    private final Logger logger = LoggerFactory.getLogger(ForeignKeyValidator.class);

    DataRepository dataManager;

    public ForeignKeyValidator(DataRepository dataRepository) {
        this.dataManager = dataRepository;
    }

    @Override
    public void check(Class<?> clazz) throws DataValidateException {
        Container container = dataManager.queryContainer(clazz, Container.class);
        if (container == null) {
            logger.warn("Container not found, skipping foreign key check: {}", clazz.getSimpleName());
            return;
        }

        // Get all fields with foreign key annotation
        List<Field> foreignKeyFields = getForeignKeyFields(clazz);
        if (foreignKeyFields.isEmpty()) {
            return;
        }
        logger.debug("Checking foreign key constraints for class {}, number of foreign key fields: {}", clazz.getSimpleName(), foreignKeyFields.size());

        for (Object record : container.getAllRecords()) {
            for (Field field : foreignKeyFields) {
                try {
                    checkForeignKeyField(record, field);
                } catch (Exception e) {
                    String errorMsg = String.format("Foreign key check failed - Class: %s, Record ID: %s, Field: %s",
                            clazz.getSimpleName(), getRecordId(record), field.getName());
                    logger.error(errorMsg, e);
                    throw new ForeignKeyConstraintException(errorMsg, e);
                }
            }
        }
    }

    /**
     * Gets record ID
     *
     * @param record the record object
     * @return the record ID
     */
    private Object getRecordId(Object record) {
        try {
            // Try to get ID via getId() method
            Method getIdMethod = record.getClass().getMethod("getId");
            return getIdMethod.invoke(record);
        } catch (Exception e) {
            // If ID cannot be obtained, return null
            return null;
        }
    }

    /**
     * Gets all fields with foreign key annotation in a class
     *
     * @param clazz the class to check
     * @return list of foreign key fields
     */
    private List<Field> getForeignKeyFields(Class<?> clazz) {
        List<Field> foreignKeyFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                foreignKeyFields.add(field);
            }
        }

        return foreignKeyFields;
    }

    /**
     * Checks a single foreign key field
     *
     * @param record the current record
     * @param field  the foreign key field
     */
    private void checkForeignKeyField(Object record, Field field) throws DataValidateException {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        Class<?> referencedClass = foreignKey.refer();

        // Get foreign key field value
        Object foreignKeyValue = getFieldValue(record, field);
        if (foreignKeyValue == null) {
            String errorMsg = String.format("Foreign key constraint violation - Class: %s, Record ID: %s, Field: %s, Foreign key value: null, Referenced table: %s",
                    record.getClass().getSimpleName(), getRecordId(record), field.getName(),
                    getTableName(referencedClass));
            throw new ForeignKeyConstraintException(errorMsg);
        }

        Container container = dataManager.queryContainer(referencedClass, Container.class);
        Set<Object> referencedIds = container.getAllKeys();
        // Check if foreign key value exists in referenced table
        if (!referencedIds.contains(foreignKeyValue)) {
            String errorMsg = String.format("Foreign key constraint violation - Class: %s, Record ID: %s, Field: %s, Foreign key value: %s, Referenced table: %s",
                    record.getClass().getSimpleName(), getRecordId(record), field.getName(),
                    foreignKeyValue, getTableName(referencedClass));
            throw new ForeignKeyConstraintException(errorMsg);
        }
    }

    private String getTableName(Class<?> tableClass) {
        if (tableClass.isAnnotationPresent(DataTable.class)) {
            return tableClass.getAnnotation(DataTable.class).name();
        }
        return tableClass.getSimpleName();
    }

    /**
     * Gets field value
     *
     * @param record the record object
     * @param field  the field
     * @return the field value
     */
    private Object getFieldValue(Object record, Field field) {
        try {
            return field.get(record);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field: " + field.getName(), e);
        }
    }
}
