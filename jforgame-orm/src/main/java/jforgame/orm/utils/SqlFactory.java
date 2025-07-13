package jforgame.orm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.orm.FieldMetadata;
import jforgame.orm.OrmBridge;
import jforgame.orm.entity.StatefulEntity;

/**
 * SQL工厂类 - 负责生成各种SQL语句
 * 使用参数化绑定以防止SQL注入
 */
class SqlFactory {

    private static final Logger logger = LoggerFactory.getLogger(SqlFactory.class);

    // SQL常量
    private static final String INSERT_INTO = "INSERT INTO ";
    private static final String UPDATE = "UPDATE ";
    private static final String DELETE_FROM = "DELETE FROM ";
    private static final String SET = " SET ";
    private static final String WHERE = " WHERE ";
    private static final String AND = " AND ";
    private static final String VALUES = " VALUES ";
    private static final String COLUMN_WRAPPER = "`";
    private static final String EQUALS = " = ";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String WHERE_1_EQ_1 = "1=1";

    /**
     * 创建插入SQL（参数化版本）
     */
    public static String createInsertPreparedSql(OrmBridge bridge) {
        List<String> properties = bridge.listAllProperties();
        List<String> columns = getColumnNames(properties, bridge);

        return INSERT_INTO + bridge.getTableName() + " (" +
                String.join(COMMA + SPACE, wrapColumns(columns)) +
                ") " + VALUES + "(" +
                String.join(COMMA + SPACE, createPlaceholders(properties.size())) +
                ")";
    }

    /**
     * 创建更新SQL（参数化版本）
     */
    public static String createUpdatePreparedSql(StatefulEntity entity, OrmBridge bridge) {
        Set<String> columns = entity.savingColumns();
        boolean saveAll = entity.isSaveAll() || columns == null || columns.isEmpty();

        List<String> updateColumns = new ArrayList<>();

        // 构建SET子句
        for (Map.Entry<String, FieldMetadata> entry : bridge.getFieldMetadataMap().entrySet()) {
            String property = entry.getKey();
            if (!saveAll && !columns.contains(property)) {
                continue;
            }

            String column = getColumnName(property, bridge);
            updateColumns.add(wrapColumn(column) + EQUALS + "?");
        }

        // 构建WHERE子句
        List<String> whereColumns = new ArrayList<>();
        for (String property : bridge.getPrimaryKeyProperties()) {
            String column = getColumnName(property, bridge);
            whereColumns.add(wrapColumn(column) + EQUALS + "?");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE).append(bridge.getTableName()).append(SET);
        sql.append(String.join(COMMA + SPACE, updateColumns));
        sql.append(WHERE).append(WHERE_1_EQ_1);
        for (String whereColumn : whereColumns) {
            sql.append(AND).append(whereColumn);
        }

        return sql.toString();
    }

    /**
     * 创建删除SQL（参数化版本）
     */
    public static String createDeletePreparedSql(OrmBridge bridge) {
        List<String> whereColumns = new ArrayList<>();

        for (String property : bridge.getPrimaryKeyProperties()) {
            String column = getColumnName(property, bridge);
            whereColumns.add(wrapColumn(column) + EQUALS + "?");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(DELETE_FROM).append(bridge.getTableName());
        sql.append(WHERE).append(WHERE_1_EQ_1);
        for (String whereColumn : whereColumns) {
            sql.append(AND).append(whereColumn);
        }

        return sql.toString();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取列名列表
     */
    private static List<String> getColumnNames(List<String> properties, OrmBridge bridge) {
        List<String> columns = new ArrayList<>();
        for (String property : properties) {
            columns.add(getColumnName(property, bridge));
        }
        return columns;
    }

    /**
     * 获取单个列名
     */
    private static String getColumnName(String property, OrmBridge bridge) {
        String override = bridge.getOverrideProperty(property);
        return override != null ? override : property;
    }

    /**
     * 包装列名
     */
    private static List<String> wrapColumns(List<String> columns) {
        List<String> wrapped = new ArrayList<>();
        for (String column : columns) {
            wrapped.add(wrapColumn(column));
        }
        return wrapped;
    }

    /**
     * 包装单个列名
     */
    private static String wrapColumn(String column) {
        return COLUMN_WRAPPER + column + COLUMN_WRAPPER;
    }

    /**
     * 创建占位符列表
     */
    private static List<String> createPlaceholders(int count) {
        List<String> placeholders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            placeholders.add("?");
        }
        return placeholders;
    }

    /**
     * 创建WHERE子句SQL
     */
    private static String createWhereClauseSql(StatefulEntity entity, OrmBridge bridge) {
        StringBuilder sb = new StringBuilder();
        sb.append(WHERE).append(WHERE_1_EQ_1);

        for (String property : bridge.getPrimaryKeyProperties()) {
            try {
                Object value = ReflectUtils.getMethodValue(entity, property);
                String column = getColumnName(property, bridge);
                sb.append(AND).append(wrapColumn(column)).append(EQUALS)
                        .append("'").append(value != null ? value.toString().replace("'", "''") : "").append("'");
            } catch (Exception e) {
                logger.error("Failed to create WHERE clause for property: {}", property, e);
            }
        }
        return sb.toString();
    }

}

