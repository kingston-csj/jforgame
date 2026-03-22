package jforgame.orm.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * 数据库表结构变更策略
 * {@link  SchemaAction}
 */
public interface SchemaStrategy {

    /**
     * 具体的执行动态
     * @param con 数据库连接，注意，这里遵循“谁创建，谁关闭（ownership原则）”，方法内部不会自动关闭conn，请确保客户端代码自动关闭
     * @param codeTables
     * @throws SQLException
     */
    void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException;
}
