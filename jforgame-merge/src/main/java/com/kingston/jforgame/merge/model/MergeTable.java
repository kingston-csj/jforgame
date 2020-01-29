package com.kingston.jforgame.merge.model;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface MergeTable {

    List<String> merge(List<Map<String,Object>> parent, List<Map<String,Object>> child);

    String getTable();

    default String toInsertSql(String tableName, Map<String, Object> data) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + tableName).append("(");
        // "INSERT INTO member(mid,name,birthday,age,note) VALUES " + " (myseq.nextval,?,?,?,?)";
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sql.append(entry.getKey()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") VALUES (");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sql.append("'").append(entry.getValue()).append("'").append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    }

    default void save(Connection conn, List<String> sqls) {
        try {
            conn.setAutoCommit(false);
            for (String sql : sqls) {
                conn.createStatement().executeQuery(sql);
            }
           conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("持久化失败");
        }
    }

}
