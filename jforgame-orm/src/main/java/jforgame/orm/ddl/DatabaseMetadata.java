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
public class DatabaseMetadata {

    private Map<String, TableMetadata> tables = new HashMap<>();

    private DatabaseMetaData metaData;

    private String types[] = new String[]{"TABLE"};

    public DatabaseMetadata(Connection conn) throws SQLException {
        this.metaData = conn.getMetaData();
    }

    public List<String> getTables(Connection conn) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        String[] types = {"TABLE"};
        ResultSet tables = databaseMetaData.getTables(null, null, "%", types);
        ArrayList<String> tablesList = new ArrayList<String>();
        while (tables.next()) {
            tablesList.add(tables.getString("TABLE_NAME"));
        }
        return tablesList;
    }

    public TableMetadata getTableMetadata(String table) {
        return tables.computeIfAbsent(table, k -> {
            try {
                ResultSet rs = metaData.getTables(null, null, "%", types);
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
