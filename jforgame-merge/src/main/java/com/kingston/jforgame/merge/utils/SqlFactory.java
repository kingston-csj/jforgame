package com.kingston.jforgame.merge.utils;

public class SqlFactory {

    public static String createClearPlayerSql(int minLevel, int noLoginDays) {
        return String.format("DELETE FROM t_role WHERE level < %d", minLevel);
    }
}
