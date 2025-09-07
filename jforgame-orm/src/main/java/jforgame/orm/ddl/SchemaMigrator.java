package jforgame.orm.ddl;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 启动时根据实体类自动更新表结构（新增字段、索引等，不删除现有字段或表），对应 "update"参数
 * {@link  SchemaAction#UPDATE}
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
            logger.info("执行schema --> {}", sql);
            con.createStatement().execute(sql);
        }
        try {
            con.close();
        } catch (Exception e) {
            logger.error("关闭数据库连接失败", e);
        }
    }

    /**
     * 生成ddl的sql语句列表
     *
     * @param tablesDef      代码的表定义
     * @param tablesMetadata 数据库的表schema
     * @return 变更ddl对应的sql列表
     */
    public List<String> createDdlSqls(Map<String, TableDefinition> tablesDef, Map<String, TableMetadata> tablesMetadata) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, TableDefinition> entry : tablesDef.entrySet()) {
            TableDefinition tableDef = entry.getValue();
            String tableName = entry.getKey();
            if (!tablesMetadata.containsKey(tableName)) {
                // 数据库不存在，建表
                result.add(tableDef.sqlCreateString());
            } else {
                // 数据库已存在，查看是否有字段新增,不处理删除的字段
                Iterator<String> it = tableDef.sqlAlterStrings(tablesMetadata.get(tableName));
                while (it.hasNext()) {
                    result.add(it.next());
                }
            }
        }

        return result;
    }

}
