package jforgame.merge.utils;

import jforgame.merge.config.MergeServer;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class JdbcUtils {

    private static Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    public static Connection getConnection(MergeServer server) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(server.getUrl(), server.getUser(), server.getPassword());
        } catch (Exception e) {
            return null;
        }
    }

    public static int queryRecordSum(Connection conn, String sql) {
        int result = -1;
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(sql);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e1) {
            logger.error("执行sql出错,sql = {}", sql);
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(stmt);
        }
        return result;
    }

    public static int execUpdate(Connection conn, String sql) {
        int result = -1;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e1) {
            logger.error("执行sql出错,sql = {}", sql);
        } finally {
            DbUtils.closeQuietly(stmt);
        }
        return result;
    }

}
