package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

/**
 * 验证数据库schema与代码定义的一致性
 * 比较数据库的DDL和代码的DDL，若有差异则抛出异常
 * 这里验证比较复杂，实现较为简单，不验证主键、外键等复杂的约束
 * 只验证代码与数据表数量差异，字段类型、字段长度、是否允许为空等简单定义
 */
public class SchemaValidator implements SchemaStrategy {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaValidator.class);

    /**
     * 执行验证策略：比较数据库schema与代码定义的一致性
     *
     * @param con        数据库连接
     * @param codeTables 代码中定义的表类集合
     * @throws SQLException              SQL异常
     * @throws SchemaValidationException 当数据库schema与代码定义不一致时抛出
     */
    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        TableConfiguration tableConfiguration = new TableConfiguration();
        tableConfiguration.register(codeTables);

        DatabaseSchema databaseMetadata = new DatabaseSchema(con);
        List<String> tables = databaseMetadata.getTables(con);

        // 获取数据库中所有表的元数据
        for (String table : tables) {
            databaseMetadata.getOrCreateTableMetadata(table);
        }

        // 验证schema一致性
        validateSchemaConsistency(tableConfiguration.getTables(), databaseMetadata.getTables());

        logger.info("数据库schema验证完成，所有表结构一致");

        try {
            con.close();
        } catch (Exception e) {
            logger.error("关闭数据库连接失败", e);
        }
    }

    /**
     * 验证数据库schema与代码定义的一致性
     *
     * @param codeTablesDef          代码中的表定义
     * @param databaseTablesMetadata 数据库中的表元数据
     * @throws SchemaValidationException 当schema不一致时抛出异常
     */
    private void validateSchemaConsistency(Map<String, TableDefinition> codeTablesDef,
                                           Map<String, TableMetadata> databaseTablesMetadata)
            throws SchemaValidationException {
        List<String> validationErrors = new ArrayList<>();

        // 检查代码中定义的表是否在数据库中存在
        for (Map.Entry<String, TableDefinition> entry : codeTablesDef.entrySet()) {
            String tableName = entry.getKey();
            TableDefinition codeTableDef = entry.getValue();

            if (!databaseTablesMetadata.containsKey(tableName)) {
                validationErrors.add("表 '" + tableName + "' 在代码中定义但数据库中不存在");
                continue;
            }

            // 验证表结构一致性
            TableMetadata dbTableMetadata = databaseTablesMetadata.get(tableName);
            validateTableStructure(tableName, codeTableDef, dbTableMetadata, validationErrors);
        }

        // 检查数据库中是否存在代码中未定义的表
        for (String dbTableName : databaseTablesMetadata.keySet()) {
            if (!codeTablesDef.containsKey(dbTableName)) {
                validationErrors.add("表 '" + dbTableName + "' 在数据库中存在但代码中未定义");
            }
        }

        // 如果有验证错误，抛出异常
        if (!validationErrors.isEmpty()) {
            String errorMessage = "数据库schema验证失败:\n" + String.join("\n", validationErrors);
            logger.error(errorMessage);
            throw new SchemaValidationException(errorMessage);
        }
    }

    /**
     * 验证单个表的结构一致性
     *
     * @param tableName        表名
     * @param codeTableDef     代码中的表定义
     * @param dbTableMetadata  数据库中的表元数据
     * @param validationErrors 验证错误列表
     */
    private void validateTableStructure(String tableName, TableDefinition codeTableDef,
                                        TableMetadata dbTableMetadata, List<String> validationErrors) {
        // 验证字段定义
        validateColumns(tableName, codeTableDef, dbTableMetadata, validationErrors);
    }

        /**
     * 验证字段定义一致性
     */
    private void validateColumns(String tableName, TableDefinition codeTableDef, 
                               TableMetadata dbTableMetadata, List<String> validationErrors) {
        Map<String, ColumnDefinition> codeColumns = codeTableDef.getColumns();
        Map<String, ColumnMetadata> dbColumns = dbTableMetadata.getColumns();

        // 创建小写映射，统一以小写进行验证
        Map<String, ColumnDefinition> codeColumnsLower = new java.util.HashMap<>();
        for (Map.Entry<String, ColumnDefinition> entry : codeColumns.entrySet()) {
            codeColumnsLower.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        Map<String, ColumnMetadata> dbColumnsLower = new java.util.HashMap<>();
        for (Map.Entry<String, ColumnMetadata> entry : dbColumns.entrySet()) {
            dbColumnsLower.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        // 检查代码中定义的字段是否在数据库中存在
        for (Map.Entry<String, ColumnDefinition> entry : codeColumnsLower.entrySet()) {
            String columnNameLower = entry.getKey();
            ColumnDefinition codeColumn = entry.getValue();

            if (!dbColumnsLower.containsKey(columnNameLower)) {
                validationErrors.add("表 '" + tableName + "' 的字段 '" + codeColumn.getName() + "' 在代码中定义但数据库中不存在");
                continue;
            }

            // 验证字段类型和属性
            ColumnMetadata dbColumn = dbColumnsLower.get(columnNameLower);
            if (!isTypeCompatible(codeColumn.getJdbcType(), dbColumn.getFullTypeName(), dbColumn.getTypeCode())) {
                validationErrors.add("表 '" + tableName + "' 的字段 '" + codeColumn.getName() + "' 类型不匹配: 代码中为 '" + 
                                  codeColumn.getJdbcType() + "', 数据库中为 '" + dbColumn.getFullTypeName() + "'");
            }

            // 比较可空性：代码中的isNullable()与数据库中的getNullable()
            boolean codeNullable = codeColumn.isNullable();
            boolean dbNullable = "YES".equalsIgnoreCase(dbColumn.getNullable());
            if (codeNullable != dbNullable) {
                validationErrors.add("表 '" + tableName + "' 的字段 '" + codeColumn.getName() + "' 可空性不匹配: 代码中为 " + 
                                  codeNullable + ", 数据库中为 " + dbNullable);
            }
        }

        // 检查数据库中是否存在代码中未定义的字段
        for (String dbColumnNameLower : dbColumnsLower.keySet()) {
            if (!codeColumnsLower.containsKey(dbColumnNameLower)) {
                // 找到原始字段名用于错误信息
                String originalDbColumnName = null;
                for (Map.Entry<String, ColumnMetadata> entry : dbColumns.entrySet()) {
                    if (entry.getKey().toLowerCase().equals(dbColumnNameLower)) {
                        originalDbColumnName = entry.getKey();
                        break;
                    }
                }
                validationErrors.add("表 '" + tableName + "' 的字段 '" + originalDbColumnName + "' 在数据库中存在但代码中未定义");
            }
        }
    }

        /**
     * 判断两个类型是否兼容
     *
     * @param codeType   代码中的类型
     * @param dbType     数据库中的类型
     * @param dbTypeCode 数据库中的类型代码
     * @return 是否兼容
     */
    private boolean isTypeCompatible(String codeType, String dbType, int dbTypeCode) {
        // 提取基础类型（去掉默认值信息）
        String codeBaseType = extractBaseType(codeType);
        String dbBaseType = extractBaseType(dbType);
        
        // 对于整数类型，忽略长度信息进行比较
        if (isIntegerType(dbTypeCode)) {
            return codeBaseType.equalsIgnoreCase(dbBaseType);
        }
        
        // 对于长文本类型，忽略长度信息进行比较
        if (isLongTextType(codeBaseType) || isLongTextType(dbBaseType)) {
            return codeBaseType.equalsIgnoreCase(dbBaseType);
        }
        
        // 对于其他类型，进行基础类型比较（忽略大小写）
        return codeBaseType.equalsIgnoreCase(dbBaseType);
    }

    /**
     * 判断是否为整数类型
     *
     * @param typeCode SQL类型代码
     * @return 是否为整数类型
     */
    private boolean isIntegerType(int typeCode) {
        return typeCode == java.sql.Types.TINYINT ||
                typeCode == java.sql.Types.SMALLINT ||
                typeCode == java.sql.Types.INTEGER ||
                typeCode == java.sql.Types.BIGINT;
    }

    /**
     * 判断是否为长文本类型
     *
     * @param typeName 类型名称
     * @return 是否为长文本类型
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
     * 提取基础类型名称（去掉长度信息和默认值信息）
     *
     * @param fullType 完整类型名称
     * @return 基础类型名称
     */
    private String extractBaseType(String fullType) {
        // 去掉默认值信息（如 "bigint DEFAULT '0'" -> "bigint"）
        String typeWithoutDefault = fullType;
        int defaultIndex = fullType.toUpperCase().indexOf(" DEFAULT ");
        if (defaultIndex > 0) {
            typeWithoutDefault = fullType.substring(0, defaultIndex).trim();
        }
        
        // 去掉长度信息（如 "varchar(256)" -> "varchar"）
        int parenIndex = typeWithoutDefault.indexOf('(');
        if (parenIndex > 0) {
            return typeWithoutDefault.substring(0, parenIndex);
        }
        return typeWithoutDefault;
    }
}