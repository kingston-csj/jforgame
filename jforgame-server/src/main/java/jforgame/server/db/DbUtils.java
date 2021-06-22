package jforgame.server.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jforgame.orm.utils.DbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 使用proxool数据源对orm-DbUtils的进一步封装
 *
 * @author kinson
 */
public class DbUtils {

    private static Logger logger = LoggerFactory.getLogger(DbUtils.class);

    /**
     * 策划数据库
     */
    public static final String DB_DATA = "config";
    /**
     * 玩家数据库
     */
    public static final String DB_USER = "user";

    private static HikariDataSource configDataSource;

    private static HikariDataSource userDataSource;

    public static void init() throws Exception {
        Properties props = new Properties();
        props.load(new FileReader("configs/jdbc.properties"));
        configDataSource = createDataSource(props, DB_DATA);
        userDataSource = createDataSource(props, DB_USER);
    }

    private static HikariDataSource createDataSource(Properties props, String db) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty(db + ".dataSource.jdbc"));
        config.setUsername(props.getProperty(db + ".dataSource.user"));
        config.setPassword(props.getProperty(db + ".dataSource.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

    /**
     * 查询返回一个bean实体
     *
     * @param alias  数据库别名
     * @param sql
     * @param entity
     * @return
     */
    public static <T> T queryOne(String alias, String sql, Class<?> entity) throws SQLException {
        Connection connection = getConnection(alias);
        return DbHelper.queryOne(connection, sql, entity);
    }

    public static <T> T queryOneById(String alias, String sql, Class<?> entity, String id) throws SQLException {
        Connection connection = getConnection(alias);
        return DbHelper.queryOne(connection, sql, entity, id);
    }


    /**
     * 查询返回bean实体列表
     *
     * @param alias  数据库别名
     * @param sql
     * @param entity
     * @return
     */
    public static <T> List<T> queryMany(String alias, String sql, Class<?> entity) throws SQLException {
        Connection connection = getConnection(alias);
        return DbHelper.queryMany(connection, sql, entity);
    }

    /**
     * 查询返回一个map
     *
     * @param alias 数据库别名
     * @param sql
     * @return
     */
    public static Map<String, Object> queryMap(String alias, String sql) throws SQLException {
        Connection connection = getConnection(alias);
        return DbHelper.queryMap(connection, sql);
    }

    /**
     * 查询返回一个map
     *
     * @param alias 数据库别名
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> queryMapList(String alias, String sql) throws SQLException {
        Connection connection = getConnection(alias);
        return DbHelper.queryMapList(connection, sql);
    }

    /**
     * 执行特定的sql语句(只有db库有执行权限)
     *
     * @param sql
     * @return
     */
    public static int executeUpdate(String sql) throws SQLException {
        Connection connection = getConnection(DB_USER);
        return DbHelper.executeUpdate(connection, sql);
    }

    public static Connection getConnection(String alias) {
        try {
            if (DB_DATA.contains(alias)) {
                return configDataSource.getConnection();
            } else if (DB_USER.contains(alias)) {
                return userDataSource.getConnection();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

}
