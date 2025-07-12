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
 * 支持参数化查询以防止SQL注入
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
    public static String createInsertSql(StatefulEntity entity, OrmBridge bridge) {
        List<String> properties = bridge.listProperties();
        List<String> columns = getColumnNames(properties, bridge);

        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(bridge.getTableName()).append(" (");
        sql.append(String.join(COMMA + SPACE, wrapColumns(columns)));
        sql.append(") ").append(VALUES).append("(");
        sql.append(String.join(COMMA + SPACE, createPlaceholders(properties.size())));
        sql.append(")");

        return sql.toString();
    }

    /**
     * 创建插入SQL（字符串拼接版本 - 不推荐使用）
     *
     * @deprecated 使用createInsertSql替代，避免SQL注入风险
     */
    @Deprecated
    public static String createInsertSqlString(StatefulEntity entity, OrmBridge bridge) {
        List<String> properties = bridge.listProperties();
        List<String> columns = getColumnNames(properties, bridge);
        List<Object> values = getFieldValues(entity, properties, bridge);

        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(bridge.getTableName()).append(" (");
        sql.append(String.join(COMMA + SPACE, wrapColumns(columns)));
        sql.append(") ").append(VALUES).append("(");
        sql.append(String.join(COMMA + SPACE, escapeValues(values)));
        sql.append(")");

        return sql.toString();
    }

    /**
     * 创建预编译插入SQL
     */
    public static String createPreparedInsertSql(StatefulEntity entity, OrmBridge bridge) {
        List<String> properties = bridge.listProperties();
        List<String> columns = getColumnNames(properties, bridge);

        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_INTO).append(bridge.getTableName()).append(" (");
        sql.append(String.join(COMMA + SPACE, wrapColumns(columns)));
        sql.append(") ").append(VALUES).append("(");
        sql.append(String.join(COMMA + SPACE, createPlaceholders(properties.size())));
        sql.append(")");

        return sql.toString();
    }

    /**
     * 创建更新SQL（参数化版本）
     */
    public static String createUpdateSql(StatefulEntity entity, OrmBridge bridge) {
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
        for (String property : bridge.getQueryProperties()) {
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
     * 创建预编译更新SQL
     */
    public static String createPreparedUpdateSql(StatefulEntity entity, OrmBridge bridge, Object[] columns) {
        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE).append(bridge.getTableName()).append(SET);
        sql.append(columnSetterSql(columns));
        sql.append(createWhereClauseSql(entity, bridge));
        return sql.toString();
    }

    /**
     * 创建删除SQL（参数化版本）
     */
    public static String createDeleteSql(StatefulEntity entity, OrmBridge bridge) {
        List<String> whereColumns = new ArrayList<>();

        for (String property : bridge.getQueryProperties()) {
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
     * 获取字段值列表
     */
    private static List<Object> getFieldValues(StatefulEntity entity, List<String> properties, OrmBridge bridge) {
        List<Object> values = new ArrayList<>();
        for (String property : properties) {
            try {
                FieldMetadata metadata = bridge.getFieldMetadataMap().get(property);
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }
                values.add(value);
            } catch (Exception e) {
                logger.error("Failed to get field value for property: " + property, e);
                values.add(null);
            }
        }
        return values;
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
     * 转义值列表（用于字符串拼接版本）
     */
    private static List<String> escapeValues(List<Object> values) {
        List<String> escaped = new ArrayList<>();
        for (Object value : values) {
            escaped.add("'" + (value != null ? value.toString().replace("'", "''") : "") + "'");
        }
        return escaped;
    }

    /**
     * 构建列设置SQL
     */
    private static String columnSetterSql(Object[] columns) {
        List<String> setters = new ArrayList<>();
        for (Object column : columns) {
            setters.add(wrapColumn(column.toString()) + EQUALS + "?");
        }
        return String.join(COMMA + SPACE, setters);
    }

    /**
     * 创建WHERE子句SQL
     */
    private static String createWhereClauseSql(StatefulEntity entity, OrmBridge bridge) {
        StringBuilder sb = new StringBuilder();
        sb.append(WHERE).append(WHERE_1_EQ_1);

        for (String property : bridge.getQueryProperties()) {
            try {
                Object value = ReflectUtils.getMethodValue(entity, property);
                String column = getColumnName(property, bridge);
                sb.append(AND).append(wrapColumn(column)).append(EQUALS)
                        .append("'").append(value != null ? value.toString().replace("'", "''") : "").append("'");
            } catch (Exception e) {
                logger.error("Failed to create WHERE clause for property: " + property, e);
            }
        }
        return sb.toString();
    }

    // ==================== 兼容性方法 ====================

    /**
     * @deprecated 使用createUpdateSql替代
     */
    @Deprecated
    public static String createUpdateSqlString(StatefulEntity entity, OrmBridge bridge) {
        Set<String> columns = entity.savingColumns();
        boolean saveAll = entity.isSaveAll() || columns == null || columns.isEmpty();

        List<String> updateParts = new ArrayList<>();
        for (Map.Entry<String, FieldMetadata> entry : bridge.getFieldMetadataMap().entrySet()) {
            String property = entry.getKey();
            if (!saveAll && !columns.contains(property)) {
                continue;
            }

            try {
                FieldMetadata metadata = entry.getValue();
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }

                String column = getColumnName(property, bridge);
                updateParts.add(wrapColumn(column) + EQUALS + "'" +
                        (value != null ? value.toString().replace("'", "''") : "") + "'");
            } catch (Exception e) {
                logger.error("Failed to create UPDATE SQL for property: " + property, e);
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE).append(bridge.getTableName()).append(SET);
        sql.append(String.join(COMMA + SPACE, updateParts));
        sql.append(createWhereClauseSql(entity, bridge));

        return sql.toString();
    }

    /**
     * @deprecated 使用createDeleteSql替代
     */
    @Deprecated
    public static String createDeleteSqlString(StatefulEntity entity, OrmBridge bridge) {
        StringBuilder sb = new StringBuilder();
        sb.append(DELETE_FROM).append(bridge.getTableName());
        sb.append(createWhereClauseSql(entity, bridge));
        return sb.toString();
    }
}

