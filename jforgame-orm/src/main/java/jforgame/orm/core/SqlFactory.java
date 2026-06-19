package jforgame.orm.core;

import jforgame.orm.entity.StatefulEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * SQL factory class - responsible for generating various SQL statements.
 * Uses parameterized binding to prevent SQL injection.
 */
class SqlFactory {

    // SQL constants
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

    /**
     * Create insert SQL (parameterized version)
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
     * Create update SQL (parameterized version)
     */
    public static String createUpdatePreparedSql(StatefulEntity entity, OrmBridge bridge) {
        Set<String> columns = entity.getAllModifiedColumns();
        boolean saveAll = entity.isSaveAll();

        List<String> updateColumns = new ArrayList<>();

        // Build SET clause
        for (String property : bridge.listAllProperties()) {
            if (!saveAll && !columns.contains(property)) {
                continue;
            }

            String column = getColumnName(property, bridge);
            updateColumns.add(wrapColumn(column) + EQUALS + "?");
        }

        if (updateColumns.isEmpty()) {
            return "";
        }

        return UPDATE + bridge.getTableName() + SET +
                String.join(COMMA + SPACE, updateColumns) +
                WHERE + createWhereClause(bridge);
    }

    private static String createWhereClause(OrmBridge bridge) {
        // Build WHERE clause
        List<String> whereColumns = new ArrayList<>();
        for (String property : bridge.getPrimaryKeyProperties()) {
            String column = getColumnName(property, bridge);
            whereColumns.add(wrapColumn(column) + EQUALS + "?");
        }
        return String.join(AND, whereColumns);
    }

    /**
     * Create delete SQL (parameterized version)
     */
    public static String createDeletePreparedSql(OrmBridge bridge) {
        return DELETE_FROM + bridge.getTableName() +
                WHERE + createWhereClause(bridge);
    }


    // ==================== Private helper methods ====================

    /**
     * Get column name list
     */
    private static List<String> getColumnNames(List<String> properties, OrmBridge bridge) {
        List<String> columns = new ArrayList<>();
        for (String property : properties) {
            columns.add(getColumnName(property, bridge));
        }
        return columns;
    }

    /**
     * Get single column name
     */
    private static String getColumnName(String property, OrmBridge bridge) {
        String override = bridge.getOverrideProperty(property);
        return override != null ? override : property;
    }

    /**
     * Wrap column names
     */
    private static List<String> wrapColumns(List<String> columns) {
        List<String> wrapped = new ArrayList<>();
        for (String column : columns) {
            wrapped.add(wrapColumn(column));
        }
        return wrapped;
    }

    /**
     * Wrap single column name
     */
    private static String wrapColumn(String column) {
        return COLUMN_WRAPPER + column + COLUMN_WRAPPER;
    }

    /**
     * Create placeholder list
     */
    private static List<String> createPlaceholders(int count) {
        List<String> placeholders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            placeholders.add("?");
        }
        return placeholders;
    }

}

