package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * 数据库表结构变更策略
 * {@link  SchemaAction}
 */
public interface SchemaStrategy {

    void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException;
}
