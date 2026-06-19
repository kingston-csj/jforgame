package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

/**
 * Validate consistency between database schema and code definitions.
 * Compares database DDL with code DDL. Throws exception if there are differences.
 * This validation is complex and the implementation is relatively simple. Does not validate complex constraints like primary keys, foreign keys, etc.
 * Only validates differences in number of tables between code and database, and simple definitions like field types, field lengths, nullability, etc.
 */
public class SchemaValidator implements SchemaStrategy {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaValidator.class);

    /**
     * Execute validation strategy: Compare consistency between database schema and code definitions
     *
     * @param con        database connection
     * @param codeTables set of table classes defined in code
     * @throws SQLException              SQL exception
     * @throws SchemaValidationException thrown when database schema is inconsistent with code definitions
     */
    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        TableConfiguration tableConfiguration = new TableConfiguration();
        tableConfiguration.register(codeTables);

        DatabaseSchema databaseMetadata = new DatabaseSchema(con);
        List<String> tables = databaseMetadata.getTables(con);

        // Get metadata of all tables in database
        for (String table : tables) {
            databaseMetadata.getOrCreateTableMetadata(table);
        }

        // Validate schema consistency
        validateSchemaConsistency(tableConfiguration.getTables(), databaseMetadata.getTables());

        logger.info("Database schema validation completed, all table structures are consistent");
    }

    /**
     * Validate consistency between database schema and code definitions
     *
     * @param codeTablesDef          table definitions in code
     * @param databaseTablesMetadata table metadata in database
     * @throws SchemaValidationException thrown when schema is inconsistent
     */
    private void validateSchemaConsistency(Map<String, TableDefinition> codeTablesDef,
                                           Map<String, TableMetadata> databaseTablesMetadata)
            throws SchemaValidationException {
        List<String> validationErrors = new ArrayList<>();

        // Check if tables defined in code exist in database
        for (Map.Entry<String, TableDefinition> entry : codeTablesDef.entrySet()) {
            String tableName = entry.getKey();
            TableDefinition codeTableDef = entry.getValue();

            if (!databaseTablesMetadata.containsKey(tableName)) {
                validationErrors.add("Table '" + tableName + "' is defined in code but does not exist in database");
                continue;
            }

            // Validate table structure consistency
            TableMetadata dbTableMetadata = databaseTablesMetadata.get(tableName);
            validateTableStructure(tableName, codeTableDef, dbTableMetadata, validationErrors);
        }

        // Check if there are tables in database that are not defined in code
        for (String dbTableName : databaseTablesMetadata.keySet()) {
            if (!codeTablesDef.containsKey(dbTableName)) {
                validationErrors.add("Table '" + dbTableName + "' exists in database but is not defined in code");
            }
        }

        // If there are validation errors, throw exception
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Database schema validation failed:\n" + String.join("\n", validationErrors);
            logger.error(errorMessage);
            throw new SchemaValidationException(errorMessage);
        }
    }

    /**
     * Validate single table structure consistency
     *
     * @param tableName        table name
     * @param codeTableDef     table definition in code
     * @param dbTableMetadata  table metadata in database
     * @param validationErrors validation error list
     */
    private void validateTableStructure(String tableName, TableDefinition codeTableDef,
                                        TableMetadata dbTableMetadata, List<String> validationErrors) {
        // Validate field definitions
        validateColumns(tableName, codeTableDef, dbTableMetadata, validationErrors);
    }

        /**
     * Validate field definition consistency
     */
    private void validateColumns(String tableName, TableDefinition codeTableDef, 
                               TableMetadata dbTableMetadata, List<String> validationErrors) {
        Map<String, ColumnDefinition> codeColumns = codeTableDef.getColumns();
        Map<String, ColumnMetadata> dbColumns = dbTableMetadata.getColumns();

        // Create lowercase mapping, validate uniformly using lowercase
        Map<String, ColumnDefinition> codeColumnsLower = new java.util.HashMap<>();
        for (Map.Entry<String, ColumnDefinition> entry : codeColumns.entrySet()) {
            codeColumnsLower.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        Map<String, ColumnMetadata> dbColumnsLower = new java.util.HashMap<>();
        for (Map.Entry<String, ColumnMetadata> entry : dbColumns.entrySet()) {
            dbColumnsLower.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // Check if fields defined in code exist in database
        for (Map.Entry<String, ColumnDefinition> entry : codeColumnsLower.entrySet()) {
            String columnNameLower = entry.getKey();
            ColumnDefinition codeColumn = entry.getValue();

            if (!dbColumnsLower.containsKey(columnNameLower)) {
                validationErrors.add("Table '" + tableName + "' field '" + codeColumn.getName() + "' is defined in code but does not exist in database");
                continue;
            }

            // Validate field type and properties
            ColumnMetadata dbColumn = dbColumnsLower.get(columnNameLower);
            if (!isTypeCompatible(codeColumn.getJdbcType(), dbColumn.getFullTypeName(), dbColumn.getTypeCode())) {
                validationErrors.add("Table '" + tableName + "' field '" + codeColumn.getName() + "' type mismatch: in code is '" + 
                                  codeColumn.getJdbcType() + "', in database is '" + dbColumn.getFullTypeName() + "'");
            }

            // Compare nullability: code's isNullable() vs database's getNullable()
            boolean codeNullable = codeColumn.isNullable();
            boolean dbNullable = "YES".equalsIgnoreCase(dbColumn.getNullable());
            if (codeNullable != dbNullable) {
                validationErrors.add("Table '" + tableName + "' field '" + codeColumn.getName() + "' nullability mismatch: in code is " + 
                                  codeNullable + ", in database is " + dbNullable);
            }
        }

        // Check if there are fields in database that are not defined in code
        for (String dbColumnNameLower : dbColumnsLower.keySet()) {
            if (!codeColumnsLower.containsKey(dbColumnNameLower)) {
                // Find original field name for error message
                String originalDbColumnName = null;
                for (Map.Entry<String, ColumnMetadata> entry : dbColumns.entrySet()) {
                    if (entry.getKey().toLowerCase().equals(dbColumnNameLower)) {
                        originalDbColumnName = entry.getKey();
                        break;
                    }
                }
                validationErrors.add("Table '" + tableName + "' field '" + originalDbColumnName + "' exists in database but is not defined in code");
            }
        }
    }

        /**
     * Check if two types are compatible
     *
     * @param codeType   type in code
     * @param dbType     type in database
     * @param dbTypeCode type code in database
     * @return whether compatible
     */
    private boolean isTypeCompatible(String codeType, String dbType, int dbTypeCode) {
        // Extract base type (remove default value information)
        String codeBaseType = extractBaseType(codeType);
        String dbBaseType = extractBaseType(dbType);
        
        // For integer types, ignore length information for comparison
        if (isIntegerType(dbTypeCode)) {
            return codeBaseType.equalsIgnoreCase(dbBaseType);
        }
        
        // For long text types, ignore length information for comparison
        if (isLongTextType(codeBaseType) || isLongTextType(dbBaseType)) {
            return codeBaseType.equalsIgnoreCase(dbBaseType);
        }
        
        // For other types, compare base types (case insensitive)
        return codeBaseType.equalsIgnoreCase(dbBaseType);
    }

    /**
     * Check if it is an integer type
     *
     * @param typeCode SQL type code
     * @return whether it is an integer type
     */
    private boolean isIntegerType(int typeCode) {
        return typeCode == java.sql.Types.TINYINT ||
                typeCode == java.sql.Types.SMALLINT ||
                typeCode == java.sql.Types.INTEGER ||
                typeCode == java.sql.Types.BIGINT;
    }

    /**
     * Check if it is a long text type
     *
     * @param typeName type name
     * @return whether it is a long text type
     */
    private boolean isLongTextType(String typeName) {
        String lowerTypeName = typeName.toLowerCase();
        return lowerTypeName.equals("longtext") ||
                lowerTypeName.equals("mediumtext") ||
                lowerTypeName.equals("text") ||
                lowerTypeName.equals("longblob") ||
                lowerTypeName.equals("mediumblob") ||
                lowerTypeName.equals("blob");
    }

    /**
     * Extract base type name (remove length information and default value information)
     *
     * @param fullType full type name
     * @return base type name
     */
    private String extractBaseType(String fullType) {
        // Remove default value information (e.g., "bigint DEFAULT '0'" -> "bigint")
        String typeWithoutDefault = fullType;
        int defaultIndex = fullType.toUpperCase().indexOf(" DEFAULT ");
        if (defaultIndex > 0) {
            typeWithoutDefault = fullType.substring(0, defaultIndex).trim();
        }
        
        // Remove length information (e.g., "varchar(256)" -> "varchar")
        int parenIndex = typeWithoutDefault.indexOf('(');
        if (parenIndex > 0) {
            return typeWithoutDefault.substring(0, parenIndex);
        }
        return typeWithoutDefault;
    }
}
