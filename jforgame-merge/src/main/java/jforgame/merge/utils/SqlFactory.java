package jforgame.merge.utils;

public class SqlFactory {

    public static String createClearPlayerSql(int minLevel, int noLoginDays) {
        return String.format("DELETE FROM t_role WHERE level < %d", minLevel);
    }

    public static String createDeleteTableSql(String tableName) {
        return String.format("DELETE FROM %s", tableName);
    }
}
