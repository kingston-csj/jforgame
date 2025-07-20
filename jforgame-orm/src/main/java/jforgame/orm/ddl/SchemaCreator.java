package jforgame.orm.ddl;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类似Hibernate的create策略实现
 * 每次启动时清空数据库并重新建表
 */
public class SchemaCreator implements SchemaStrategy {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaCreator.class);

    /**
     * 执行create策略：清空数据库并重新建表
     *
     * @param con        数据库连接
     * @param codeTables 代码中定义的表类集合
     * @throws SQLException SQL异常
     */
    @Override
    public void doExecute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        // 关闭自动提交，开启事务
        boolean autoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try {
            TableConfiguration tableConfiguration = new TableConfiguration();
            tableConfiguration.register(codeTables);

            DatabaseSchema databaseMetadata = new DatabaseSchema(con);
            List<String> tables = databaseMetadata.getTables(con);

            // 1. 删除所有现有表
            dropAllTables(con, tables);
            // 2. 重新创建所有表
            createAllTables(con, tableConfiguration.getTables());

            // 提交事务
            con.commit();
            logger.info("数据库schema创建完成");
        } catch (SQLException e) {
            // 回滚事务
            con.rollback();
            logger.error("数据库schema创建失败，已回滚", e);
            throw e;
        } finally {
            // 恢复自动提交设置
            con.setAutoCommit(autoCommit);
            try {
                con.close();
            } catch (Exception e) {
                logger.error("关闭数据库连接失败", e);
            }
        }
    }

    /**
     * 删除所有表
     *
     * @param con            数据库连接
     * @param existingTables 现有表名列表
     * @throws SQLException SQL异常
     */
    private void dropAllTables(Connection con, List<String> existingTables) throws SQLException {
        if (existingTables.isEmpty()) {
            logger.info("数据库中没有表需要删除");
            return;
        }
        // 禁用外键约束检查
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        }
        for (String tableName : existingTables) {
            String dropSql = "DROP TABLE IF EXISTS `" + tableName + "`";
            logger.info("执行schema --> {}", dropSql);

            try (Statement stmt = con.createStatement()) {
                stmt.execute(dropSql);
            } catch (SQLException e) {
                logger.warn("删除表 {} 失败: {}", tableName, e.getMessage());
                // 继续删除其他表，不中断流程
            }
        }
        // 重新启用外键约束检查
        try (Statement stmt = con.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    /**
     * 创建所有表
     *
     * @param con       数据库连接
     * @param tablesDef 表定义映射
     * @throws SQLException SQL异常
     */
    private void createAllTables(Connection con, Map<String, TableDefinition> tablesDef) throws SQLException {
        if (tablesDef.isEmpty()) {
            logger.info("没有表需要创建");
            return;
        }

        Set<String> sortedTableNames = tablesDef.keySet();

        for (String tableName : sortedTableNames) {
            TableDefinition tableDef = tablesDef.get(tableName);
            String createSql = tableDef.sqlCreateString();
            logger.info("执行schema --> {}", createSql);

            try (Statement stmt = con.createStatement()) {
                stmt.execute(createSql);
            } catch (SQLException e) {
                logger.error("创建表 {} 失败: {}", tableName, e.getMessage());
                throw e; // 创建表失败时中断流程
            }
        }
    }

}