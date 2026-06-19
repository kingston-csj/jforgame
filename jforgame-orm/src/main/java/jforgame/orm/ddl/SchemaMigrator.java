package jforgame.orm.ddl;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Auto update table structure based on entity classes on startup (add fields, indexes, etc., do not delete existing fields or tables), corresponds to "update" parameter.
 * {@link SchemaAction#UPDATE}
 */
public class SchemaMigrator implements SchemaStrategy {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaMigrator.class);

    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        TableConfiguration tableConfiguration = new TableConfiguration();
        tableConfiguration.register(codeTables);

        DatabaseSchema databaseMetadata = new DatabaseSchema(con);
        List<String> tables = databaseMetadata.getTables(con);
        for (String table : tables) {
            databaseMetadata.getOrCreateTableMetadata(table);
        }
        List<String> sqls = createDdlSqls(tableConfiguration.getTables(), databaseMetadata.getTables());

        for (String sql : sqls) {
            logger.info("Executing schema --> {}", sql);
            try (Statement stmt = con.createStatement()) {
                stmt.execute(sql);
            }
        }
    }

    /**
     * Generate DDL SQL statements list
     *
     * @param tablesDef      table definitions in code
     * @param tablesMetadata database table schemas
     * @return list of DDL SQL statements for changes
     */
    public List<String> createDdlSqls(Map<String, TableDefinition> tablesDef, Map<String, TableMetadata> tablesMetadata) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, TableDefinition> entry : tablesDef.entrySet()) {
            TableDefinition tableDef = entry.getValue();
            String tableName = entry.getKey();
            if (!tablesMetadata.containsKey(tableName)) {
                // Table does not exist in database, create it
                result.add(tableDef.sqlCreateString());
            } else {
                // Table exists in database, check if there are new fields. Do not handle deleted fields
                Iterator<String> it = tableDef.sqlAlterStrings(tablesMetadata.get(tableName));
                while (it.hasNext()) {
                    result.add(it.next());
                }
            }
        }

        return result;
    }

}
