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

    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        boolean autoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            TableConfiguration tableConfiguration = new TableConfiguration();
            tableConfiguration.register(codeTables);

            DatabaseSchema databaseMetadata = new DatabaseSchema(con);
            List<String> tables = databaseMetadata.getTables(con);

            dropAllTables(con, tables);
            createAllTables(con, tableConfiguration.getTables());
            createAllIndexes(con, tableConfiguration.getTables());

            con.commit();
            logger.info("Database schema creation completed");
        } catch (SQLException e) {
            con.rollback();
            logger.error("Database schema creation failed, rolled back", e);
            throw e;
        } finally {
            con.setAutoCommit(autoCommit);
        }
    }

    private void dropAllTables(Connection con, List<String> existingTables) throws SQLException {
        if (existingTables.isEmpty()) {
            logger.info("No tables to drop in database");
            return;
        }
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
            }
        }
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

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
                throw e;
            }
        }
    }

    private void createAllIndexes(Connection con, Map<String, TableDefinition> tablesDef) throws SQLException {
        if (tablesDef.isEmpty()) {
            return;
        }
        for (TableDefinition tableDef : tablesDef.values()) {
            List<String> indexSqls = tableDef.sqlCreateIndexStrings();
            for (String indexSql : indexSqls) {
                logger.info("Executing schema --> {}", indexSql);
                try (Statement stmt = con.createStatement()) {
                    stmt.execute(indexSql);
                } catch (SQLException e) {
                    logger.error("Failed to create index on table {}: {}", tableDef.getTableName(), e.getMessage());
                    throw e;
                }
            }
        }
    }

}
