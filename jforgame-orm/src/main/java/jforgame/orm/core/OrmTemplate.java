package jforgame.orm.core;

import jforgame.commons.StringUtil;
import jforgame.orm.entity.StatefulEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供所有数据表CRUD接口，以及执行任意sql的接口
 * 数据库相关方法
 * 注意：此类的所有crud方法都会自动关闭数据库连接，避免用在事务环境！！
 * 游戏领域不需要事务，如果需要事务，不要使用此类方法！！
 */
public class OrmTemplate {

    private static Logger logger = LoggerFactory.getLogger(OrmTemplate.class);

    private final DataSource dataSource;

    public OrmTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 查询返回指定类型的第一条实体
     *
     * @param sql
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T queryOne(String sql, Class<?> entity) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            throw new SQLException("sql argument is null");
        }
        if (entity == null) {
            throw new SQLException("entity argument is null");
        }
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                return (T) new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryOne failed", e);
            throw new SQLException(e);
        }
        return null;
    }

    public <T> T queryOne(String sql, Class<?> entity, String id) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            throw new SQLException("sql argument is null");
        }
        if (entity == null) {
            throw new SQLException("entity argument is null");
        }
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    return (T) new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
                }
            }
        } catch (Exception e) {
            logger.error("DbUtils queryOne failed", e);
            throw new SQLException(e);
        }
        return null;
    }

    /**
     * 查询返回bean实体列表
     *
     * @param sql
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryMany(String sql, Class<?> entity) throws SQLException {
        List<T> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            Object bean = null;
            while (resultSet.next()) {
                bean = new BeanProcessor().toBean(resultSet, entity);
                result.add((T) bean);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMany failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * 查询返回一个map
     *
     * @param sql
     * @return
     */
    public Map<String, Object> queryMap(String sql) throws SQLException {
        Map<String, Object> result = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int cols = rsmd.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if ((null == columnName) || (columnName.isEmpty())) {
                        columnName = rsmd.getColumnName(i);
                    }
                    result.put(columnName, rs.getObject(i));
                }
                break;
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMap failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * 查询返回map列表
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryMapList(String sql) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int cols = rsmd.getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if ((null == columnName) || (columnName.isEmpty())) {
                        columnName = rsmd.getColumnName(i);
                    }
                    map.put(columnName, rs.getObject(i));
                }
                result.add(map);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMapList failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * 执行特定的sql语句（防止sql注入!!!）
     *
     * @param sql
     * @return
     * @see Statement#execute(String)
     */
    public boolean executeSql(String sql) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            return true;
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        }
    }

    /**
     * 执行update语句（防止sql注入!!!）
     *
     * @param sql
     * @return
     * @see Statement#executeUpdate(String)
     */
    public int executeUpdate(String sql) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            return 0;
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            return statement.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        }
    }

    public int executeInsert(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // 获取参数化SQL
            String sql = SqlFactory.createInsertPreparedSql(bridge);
            // 获取参数值
            List<Object> parameters = SqlParameterUtils.getInsertParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // 设置参数
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        }
    }

    public int executeUpdate(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // 获取参数化SQL
            String sql = SqlFactory.createUpdatePreparedSql(entity, bridge);
            // 获取参数值
            List<Object> parameters = SqlParameterUtils.getUpdateParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // 设置参数
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        }
    }

    public int executeDelete(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // 获取参数化SQL
            String sql = SqlFactory.createDeletePreparedSql(bridge);
            // 获取参数值
            List<Object> parameters = SqlParameterUtils.getDeleteParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // 设置参数
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        }
    }

}