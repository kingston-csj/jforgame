package jforgame.orm.ddl;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchemaUpdate {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(SchemaUpdate.class);

    public void execute(Connection con, Set<Class<?>> codeTables) throws SQLException {
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
    }

    /**
     * 生产ddl的sql语句列表
     *
     * @param tablesDef      代码的表定义
     * @param tablesMetadata 数据库的表schema
     * @return
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
