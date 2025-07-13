package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库schema
 */
class DatabaseMetadata {

    private Map<String, TableMetadata> tables = new HashMap<>();

    private DatabaseMetaData metaData;

    private String types[] = new String[]{"TABLE"};

    public DatabaseMetadata(Connection conn) throws SQLException {
        this.metaData = conn.getMetaData();
    }

    public List<String> getTables(Connection conn) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        String[] types = {"TABLE"};

        // 获取当前连接的数据库schema
        String catalog = conn.getCatalog();
        String schema = conn.getSchema();

        // 如果catalog为null，尝试从URL中获取数据库名
        if (catalog == null) {
            String url = conn.getMetaData().getURL();
            // 从JDBC URL中提取数据库名
            // 格式通常是: jdbc:mysql://host:port/database_name
            if (url.contains("/")) {
                String[] parts = url.split("/");
                if (parts.length > 1) {
                    String lastPart = parts[parts.length - 1];
                    // 移除可能的参数部分
                    if (lastPart.contains("?")) {
                        lastPart = lastPart.split("\\?")[0];
                    }
                    catalog = lastPart;
                }
            }
        }

        ResultSet tables = databaseMetaData.getTables(catalog, schema, "%", types);
        ArrayList<String> tablesList = new ArrayList<String>();
        while (tables.next()) {
            tablesList.add(tables.getString("TABLE_NAME"));
        }
        return tablesList;
    }

    public TableMetadata getTableMetadata(String table) {
        return tables.computeIfAbsent(table, k -> {
            try {
                // 同样需要指定正确的catalog和schema
                String catalog = null;
                String schema = null;

                // 尝试从连接获取catalog
                try {
                    catalog = metaData.getConnection().getCatalog();
                } catch (Exception e) {
                    // 忽略异常，使用null
                }

                ResultSet rs = metaData.getTables(catalog, schema, "%", types);
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (tableName.equalsIgnoreCase(table)) {
                        TableMetadata tableMetadata = new TableMetadata(rs, metaData, true);
                        return tableMetadata;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Map<String, TableMetadata> getTables() {
        return tables;
    }
}
