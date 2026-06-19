package jforgame.orm.ddl;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Similar to Hibernate's create strategy implementation.
 * Drop all tables and recreate on every startup.
 */
public class SchemaCreator implements SchemaStrategy {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaCreator.class);

    /**
     * Execute create strategy: Drop all tables and recreate
     *
     * @param con        database connection
     * @param codeTables set of table classes defined in code
     * @throws SQLException SQL exception
     */
    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        // Disable auto-commit, start transaction
        boolean autoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            TableConfiguration tableConfiguration = new TableConfiguration();
            tableConfiguration.register(codeTables);

            DatabaseSchema databaseMetadata = new DatabaseSchema(con);
            List<String> tables = databaseMetadata.getTables(con);

            // 1. Drop all existing tables
            dropAllTables(con, tables);
            // 2. Recreate all tables
            createAllTables(con, tableConfiguration.getTables());

            // Commit transaction
            con.commit();
            logger.info("Database schema creation completed");
        } catch (SQLException e) {
            // Rollback transaction
            con.rollback();
            logger.error("Database schema creation failed, rolled back", e);
            throw e;
        } finally {
            // Restore auto-commit setting
            con.setAutoCommit(autoCommit);
        }
    }

    /**
     * Drop all tables
     *
     * @param con            database connection
     * @param existingTables list of existing table names
     * @throws SQLException SQL exception
     */
    private void dropAllTables(Connection con, List<String> existingTables) throws SQLException {
        if (existingTables.isEmpty()) {
            logger.info("No tables to drop in database");
            return;
        }
        // Disable foreign key constraint check
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        }
        for (String tableName : existingTables) {
            String dropSql = "DROP TABLE IF EXISTS `" + tableName + "`";
            logger.info("Executing schema --> {}", dropSql);

            try (Statement stmt = con.createStatement()) {
                stmt.execute(dropSql);
            } catch (SQLException e) {
                logger.warn("Failed to drop table {}: {}", tableName, e.getMessage());
                // Continue to drop other tables, do not interrupt the process
            }
        }
        // Re-enable foreign key constraint check
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * Create all tables
     *
     * @param con       database connection
     * @param tablesDef table definition map
     * @throws SQLException SQL exception
     */
    private void createAllTables(Connection con, Map<String, TableDefinition> tablesDef) throws SQLException {
        if (tablesDef.isEmpty()) {
            logger.info("No tables to create");
            return;
        }

        Set<String> sortedTableNames = tablesDef.keySet();

        for (String tableName : sortedTableNames) {
            TableDefinition tableDef = tablesDef.get(tableName);
            String createSql = tableDef.sqlCreateString();
            logger.info("Executing schema --> {}", createSql);

            try (Statement stmt = con.createStatement()) {
                stmt.execute(createSql);
            } catch (SQLException e) {
                logger.error("Failed to create table {}: {}", tableName, e.getMessage());
                throw e; // Interrupt process when table creation fails
            }
        }
    }

}
