package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * Database table structure change strategy.
 * {@link SchemaAction}
 */
public interface SchemaStrategy {

    /**
     * Specific execution logic.
     * @param con database connection. Note: follows the "who creates, who closes" ownership principle. This method will not automatically close the connection. Make sure the client code closes it.
     * @param codeTables
     * @throws SQLException
     */
    void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException;
}
