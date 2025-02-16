package jforgame.orm.ddl;

import jforgame.commons.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchemaUpdate {

    public void execute(Connection con, Set<Class<?>> codeTables) throws SQLException {
        TableConfiguration tableConfiguration = new TableConfiguration();
        tableConfiguration.register(codeTables);

        DatabaseMetadata databaseMetadata = new DatabaseMetadata(con);
        List<String> tables = databaseMetadata.getTables(con);
        for (String table : tables) {
            if (table.endsWith("ent")) {
                databaseMetadata.getTableMetadata(table);
            }
        }

        List<String> sqls = createDdlSqls(tableConfiguration.getTables(), databaseMetadata.getTables());

        for (String sql : sqls) {
            con.createStatement().execute(sql);
            System.out.println("执行schema --> " + sql);
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
                Pair<Iterator<String>, String> iteratorStrPair = tableDef.sqlAlterStrings(tablesMetadata.get(tableName));
                Iterator<String> it = iteratorStrPair.getFirst();
                while (it.hasNext()) {
                    result.add(it.next());
                }
                String lastColumn = iteratorStrPair.getSecond();
                if (lastColumn != null) {
                    String sql = "ALTER TABLE " + tableName + " MODIFY COLUMN createTime BIGINT(20) DEFAULT NULL  AFTER " + lastColumn;
                    result.add(sql);
                    result.add("ALTER TABLE " + tableName + " MODIFY COLUMN updateTime BIGINT(20) DEFAULT NULL AFTER createTime");
                }
            }
        }

        return result;
    }

}
