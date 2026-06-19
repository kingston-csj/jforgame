package jforgame.orm;

import jforgame.commons.util.ClassScanner;
import jforgame.orm.core.OrmProcessor;
import jforgame.orm.core.OrmProperties;
import jforgame.orm.ddl.SchemaAction;
import jforgame.orm.ddl.SchemaCreator;
import jforgame.orm.ddl.SchemaMigrator;
import jforgame.orm.ddl.SchemaValidator;
import jforgame.orm.entity.BaseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Set;

/**
 * OrmEngine entry point
 */
public class OrmEngine {

    /**
     * Start the engine
     * @param properties configuration properties
     * @param dataSource database data source
     * @throws Exception sql exception
     */
    public static void run(OrmProperties properties, DataSource dataSource) throws Exception {
        // Initialize orm framework
        OrmProcessor.INSTANCE.initOrmBridges(properties.getEntityPath());
        // Get all entity classes
        Set<Class<?>> codeTables = ClassScanner.listAllSubclasses(properties.getEntityPath(), BaseEntity.class);

        // Execute DDL schema operations according to the configured strategy
        executeDdlSchema(dataSource, codeTables, properties.getDdlAuto());
    }

    private static void executeDdlSchema(DataSource dataSource, Set<Class<?>> codeTables, String ddlAuto) throws Exception {
        if (SchemaAction.NONE.name().equalsIgnoreCase(ddlAuto)) {
            return;
        }
        if (SchemaAction.UPDATE.name().equalsIgnoreCase(ddlAuto)) {
            // Auto update database schema (incremental update)
            try (Connection con = dataSource.getConnection()) {
                new SchemaMigrator().doExecute(con, codeTables);
            }
        } else if (SchemaAction.CREATE.name().equalsIgnoreCase(ddlAuto)) {
            // Drop all tables and recreate
            try (Connection con = dataSource.getConnection()) {
                new SchemaCreator().doExecute(con, codeTables);
            }
        } else if (SchemaAction.VALIDATE.name().equalsIgnoreCase(ddlAuto)) {
            // Validate table structure
            try (Connection con = dataSource.getConnection()) {
                new SchemaValidator().doExecute(con, codeTables);
            }
        }
    }

}
