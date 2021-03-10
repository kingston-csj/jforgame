package jforgame.merge.service;

import jforgame.merge.config.MergeServer;
import jforgame.merge.model.MergeTable;
import jforgame.merge.utils.JdbcUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MergeService {

    private static MergeService self = new MergeService();

    private static Logger logger = LoggerFactory.getLogger(MergeService.class);

    public static MergeService getInstance() {
        return self;
    }

    public void doMerge(MergeServer parentServer, MergeServer childServer) throws Exception {
        // 母服合服前需要执行的sql
        List<String> beforeSqls = MergedTableRegistry.getInstance().getParentBeforeSqls();
        Connection parentConn = JdbcUtils.getConnection(parentServer);
        for (String sql : beforeSqls) {
            JdbcUtils.execUpdate(parentConn, sql);
        }
        // 直接合并的表
        List<String> tables = MergedTableRegistry.getInstance().listToMergeDirectlyTables();
        for (String table : tables) {
            mergeTableDirectly(parentServer, childServer, table);
        }
        // 交叉合并的表
        List<String> crossTables = MergedTableRegistry.getInstance().listToMergeCrossTables();
        for (String table : crossTables) {
            mergeTableCross(parentServer, childServer, table);
        }

        // 母服合服后需要执行的sql
        List<String> afterSqls = MergedTableRegistry.getInstance().getParentAfterSqls();
        for (String sql : afterSqls) {
            JdbcUtils.execUpdate(parentConn, sql);
        }
    }

    private void mergeTableDirectly(MergeServer parentServer, MergeServer childServer, String tableName) throws SQLException {
        logger.info("开始合并[{}]服{}表到{}服", childServer.getServerId(), tableName, parentServer.getServerId());
        Connection childConn = JdbcUtils.getConnection(childServer);
        int childRecordSum = JdbcUtils.queryRecordSum(childConn, String.format("SELECT COUNT(1) FROM %s", tableName));
        int countThreshold = 400; //超过该阈值需要多线程执行

        final int MAX_THREAD_SUM = 6;//合并的最大线程数量
        int threadSum = 1;
        if (childRecordSum > countThreshold) {
            threadSum = Math.min(MAX_THREAD_SUM, Runtime.getRuntime().availableProcessors());
        }
        int pageSize = childRecordSum % threadSum == 0 ? childRecordSum / threadSum : childRecordSum / threadSum + 1;
        logger.info("{}表总共有{}条记录,分成{}条线程进行合并，每条线程处理{}记录", tableName, childRecordSum, threadSum, pageSize);

        try {
            CountDownLatch latch = new CountDownLatch(threadSum);
            for (int i = 0; i < threadSum; i++) {
                MergeTask task = new MergeTask();
                task.parentServer = parentServer;
                task.childServer = childServer;
                task.tableName = tableName;
                task.index = i;
                task.pageSize = pageSize;
                new Thread(task).start();
            }
            latch.await();

        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    private static class MergeTask implements Runnable {

        MergeServer parentServer;
        MergeServer childServer;
        String tableName;
        int index;
        int pageSize;

        @Override
        public void run() {
            Connection targetConn = JdbcUtils.getConnection(parentServer);
            Connection childConn = JdbcUtils.getConnection(childServer);
            Map<String, Integer> tableMeta = tableFileMeta(targetConn, tableName);
            PreparedStatement parentPStat = null;
            ResultSet childRs = null;
            try {
                String insertSql = createInsertSql(tableMeta, tableName);
                parentPStat = targetConn.prepareStatement(insertSql);
                childRs = childConn.createStatement().executeQuery(String.format("SELECT * FROM %s LIMIT %d, %d", tableName, index * pageSize, pageSize));
                targetConn.setAutoCommit(false);
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
                            RenameService renameService = RenameService.getInstance();
                            // 处理角色重名
                            if ("t_role".equalsIgnoreCase(tableName) && "name".equalsIgnoreCase(fieldName)) {
                                if (renameService.getUsedPlayerNames().contains(fileValue)) {
                                    fileValue += RenameService.getInstance().getNextNameSuff();
                                }
                                RenameService.getInstance().addPlayerName(fileValue);
                            }
                            // 处理战盟重名
                            if ("t_party".equalsIgnoreCase(tableName) && "name".equalsIgnoreCase(fieldName)) {
                                if (renameService.getUsedGuildNames().contains(fileValue)) {
                                    fileValue += RenameService.getInstance().getNextNameSuff();
                                }
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
            } catch (Exception e) {
                logger.error("", e);
                System.exit(1);
            } finally {
                DbUtils.closeQuietly(parentPStat);
                DbUtils.closeQuietly(childRs);
                try {
                    DbUtils.close(targetConn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    DbUtils.close(childConn);
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

        private String createInsertSql(Map<String, Integer> tableMeta, String tableName) throws SQLException {
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
    }


    private void mergeTableCross(MergeServer parentServer, MergeServer childServer, String tableName) throws SQLException {
        logger.info("开始合并[{}]服{}表到{}服", childServer.getServerId(), tableName, parentServer.getServerId());
        Connection parentConn = JdbcUtils.getConnection(parentServer);
        Connection childConn = JdbcUtils.getConnection(childServer);
        MergeTable mergeTable = MergedTableRegistry.getInstance().getTableMergeBehavior(tableName);
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
