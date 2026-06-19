package jforgame.orm.core;

import java.util.*;

/**
 * Bridge between object and database table record
 */
public class OrmBridge {
    /**
     * Corresponding database table name
     */
    private String tableName;
    /**
     * Cache of all table fields and their corresponding metadata
     */
    private final Map<String, FieldMetaData> fieldMetadataMap = new HashMap<>();
    /**
     * Mapping of overridden property to table column
     */
    private Map<String, String> propertyToColumnOverride = new HashMap<>();
    /**
     * Mapping of overridden table column to property
     */
    private Map<String, String> columnToPropertyOverride = new HashMap<>();
    /**
     * All primary key fields of the entity, including composite primary keys
     */
    private final Set<String> uniqueProperties = new HashSet<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addFieldMetadata(String field, FieldMetaData metadata) {
        fieldMetadataMap.put(field, metadata);
    }

    public Map<String, FieldMetaData> getFieldMetadataMap() {
        return fieldMetadataMap;
    }

    /**
     * Return composite primary key list of the entity
     *
     * @return all primary key fields of the entity
     */
    public List<String> getPrimaryKeyProperties() {
        List<String> result = new ArrayList<>(this.uniqueProperties);
        Collections.sort(result);
        return result;
    }

    public void addUniqueKey(String id) {
        this.uniqueProperties.add(id);
    }

    public void addPropertyColumnOverride(String property, String column) {
        this.propertyToColumnOverride.put(property, column);
        this.columnToPropertyOverride.put(column, property);
    }

    public String getOverrideProperty(String property) {
        return this.propertyToColumnOverride.get(property);
    }

    public Map<String, String> getPropertyToColumnOverride() {
        return propertyToColumnOverride;
    }

    public Map<String, String> getColumnToPropertyOverride() {
        return columnToPropertyOverride;
    }

    public void setColumnToPropertyOverride(Map<String, String> columnToPropertyOverride) {
        this.columnToPropertyOverride = columnToPropertyOverride;
    }

    public void setPropertyToColumnOverride(Map<String, String> propertyToColumnOverride) {
        this.propertyToColumnOverride = propertyToColumnOverride;
    }

    public List<String> listAllProperties() {
        List<String> result = new ArrayList<>(this.fieldMetadataMap.keySet());
        Collections.sort(result);
        return result;
    }

}
