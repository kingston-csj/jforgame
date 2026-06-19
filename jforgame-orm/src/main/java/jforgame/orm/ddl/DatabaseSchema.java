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
 * Database schema
 */
class DatabaseSchema {

    /**
     * Cache of table metadata under this database
     */
    private Map<String, TableMetadata> tables = new HashMap<>();

    private DatabaseMetaData metaData;

    private String types[] = new String[]{"TABLE"};

    public DatabaseSchema(Connection conn) throws SQLException {
        this.metaData = conn.getMetaData();
    }

    /**
     * Get database tables
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public List<String> getTables(Connection conn) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        String[] types = {"TABLE"};

        // Get the database schema for the current connection
        String catalog = conn.getCatalog();
        String schema = conn.getSchema();

        // If catalog is null, try to get database name from URL
        if (catalog == null) {
            String url = conn.getMetaData().getURL();
            // Extract database name from JDBC URL
            // Format is usually: jdbc:mysql://host:port/database_name
            if (url.contains("/")) {
                String[] parts = url.split("/");
                if (parts.length > 1) {
                    String lastPart = parts[parts.length - 1];
                    // Remove possible parameter part
                    if (lastPart.contains("?")) {
                        lastPart = lastPart.split("\\?")[0];
                    }
                    catalog = lastPart;
                }
            }
        }

        ResultSet tables = databaseMetaData.getTables(catalog, schema, "%", types);
        ArrayList<String> tablesList = new ArrayList<>();
        while (tables.next()) {
            tablesList.add(tables.getString("TABLE_NAME"));
        }
        return tablesList;
    }

    public TableMetadata getOrCreateTableMetadata(String table) {
        return tables.computeIfAbsent(table, k -> {
            try {
                // Similarly need to specify correct catalog and schema
                String catalog = null;
                String schema = null;

                // Try to get catalog from connection
                try {
                    catalog = metaData.getConnection().getCatalog();
                } catch (Exception e) {
                    // Ignore exception, use null
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
