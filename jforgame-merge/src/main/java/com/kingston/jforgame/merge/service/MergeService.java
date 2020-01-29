package com.kingston.jforgame.merge.service;

import com.kingston.jforgame.merge.config.MergeServer;
import com.kingston.jforgame.merge.model.MergeTable;
import com.kingston.jforgame.merge.utils.JdbcUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MergeService {

    private static MergeService self = new MergeService();

    private static Logger logger = LoggerFactory.getLogger(MergeService.class);

    public static MergeService getInstance() {
        return self;
    }

    public void doMerge(MergeServer parentServer, MergeServer childServer) throws Exception {
        // 直接合并到的表
        List<String> tables = MergedTableRegister.getInstance().listToMergeDirectlyTables();
        for (String table : tables) {
            mergeTableDirectly(parentServer, childServer, table);
        }
        // 交叉合并到的表
        List<String> crossTables = MergedTableRegister.getInstance().listToMergeCrossTables();
        for (String table : crossTables) {
            mergeTableCross(parentServer, childServer, table);
        }
    }

    private void mergeTableDirectly(MergeServer parentServer, MergeServer childServer, String tableName) throws SQLException {
        logger.info("开始合并[{}]服{}表到{}服", childServer.getServerId(), tableName, parentServer.getServerId());
        Connection targetConn = JdbcUtils.getConnection(parentServer);
        Map<String, Integer> tableMeta = tableFileMeta(targetConn, tableName);
        String insertSql = createInsertSql(tableMeta, tableName);
        PreparedStatement parentPStat = targetConn.prepareStatement(insertSql);
        Connection childConn = JdbcUtils.getConnection(childServer);
        ResultSet childRs = childConn.createStatement().executeQuery("SELECT * FROM " + tableName);
        targetConn.setAutoCommit(false);
        try {
            while (childRs.next()) {
                int index = 1;
                for (Map.Entry<String, Integer> entry : tableMeta.entrySet()) {
                    int type = entry.getValue();
                    String fieldName = entry.getKey();
                    if (type == Types.BIT) {
                        parentPStat.setInt(index, childRs.getInt(fieldName));
                    } else if (type == Types.TINYINT) {
                        parentPStat.setInt(index, childRs.getInt(fieldName));
                    } else if (type == Types.SMALLINT) {
                        parentPStat.setInt(index, childRs.getInt(fieldName));
                    } else if (type == Types.INTEGER) {
                        parentPStat.setInt(index, childRs.getInt(fieldName));
                    } else if (type == Types.DATE) {
                        parentPStat.setDate(index, childRs.getDate(fieldName));
                    } else if (type == Types.LONGVARCHAR) {
                        parentPStat.setString(index, childRs.getString(fieldName));
                    } else if (type == Types.BIGINT) {
                        parentPStat.setLong(index, childRs.getLong(fieldName));
                    } else if (type == Types.FLOAT) {
                        parentPStat.setFloat(index, childRs.getFloat(fieldName));
                    } else if (type == Types.VARCHAR) {
                        String fileValue = childRs.getString(fieldName);
                        // 处理角色重名
                        if ("t_role".equalsIgnoreCase(tableName)) {
                            fileValue += RenameService.getInstance().getNextNameSuff();
                            RenameService.getInstance().addPlayerName(fileValue);
                        }
                        // 处理战盟重名
                        if ("t_party".equalsIgnoreCase(tableName)) {
                            fileValue += RenameService.getInstance().getNextNameSuff();
                            RenameService.getInstance().addGuildName(fileValue);
                        }
                        parentPStat.setString(index, fileValue);
                    } else if (type == Types.TIMESTAMP) {
                        parentPStat.setTimestamp(index, childRs.getTimestamp(fieldName));
                    } else {
                        System.out.println("uncheck " + type + ", " + fieldName);
                        throw new RuntimeException("sql类型未捕捉");
                    }
                    index++;
                }


                parentPStat.executeUpdate();
            }

            targetConn.commit();
        } finally {
            DbUtils.closeQuietly(parentPStat);
            DbUtils.closeQuietly(childRs);
            try {
                DbUtils.close(targetConn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Integer> tableFileMeta(Connection conn, String tableName) {
        Map<String, Integer> result = new LinkedHashMap<>();
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + tableName + " LIMIT 0");
            ResultSetMetaData rss = rs.getMetaData();
            int columnCount = rss.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                result.put(rss.getColumnName(i), rss.getColumnType(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String createInsertSql(Map<String, Integer> tableMeta, String tableName) throws SQLException {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO " + tableName).append("(");
        // "INSERT INTO member(mid,name,birthday,age,note) VALUES " + " (myseq.nextval,?,?,?,?)";
        for (Map.Entry<String, Integer> entry : tableMeta.entrySet()) {
            sql.append(entry.getKey()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") VALUES (");
        for (Map.Entry<String, Integer> entry : tableMeta.entrySet()) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    }

    private void mergeTableCross(MergeServer parentServer, MergeServer childServer, String tableName) throws SQLException {
        logger.info("开始合并[{}]服{}表到{}服", childServer.getServerId(), tableName, parentServer.getServerId());
        Connection parentConn = JdbcUtils.getConnection(parentServer);
        Connection childConn = JdbcUtils.getConnection(childServer);
        MergeTable mergeTable = MergedTableRegister.getInstance().getTableMergeBehavior(tableName);
        List<Map<String, Object>> map1 = new QueryRunner().query(parentConn, "SELECT * FROM " + tableName, new MapListHandler());
        List<Map<String, Object>> map2 = new QueryRunner().query(childConn, "SELECT * FROM " + tableName, new MapListHandler());

        List<String> sqls = mergeTable.merge(map1, map2);
        parentConn.setAutoCommit(false);
        for (String sql : sqls) {
            parentConn.createStatement().executeUpdate(sql);
        }
        parentConn.commit();
    }

}
