package jforgame.orm.core;

import jforgame.commons.util.StringUtil;
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
 * Provides all data table CRUD interfaces and arbitrary sql execution interface.
 * Database related methods.
 * Note: All CRUD methods in this class will automatically close database connections. Avoid using in transaction environment!!
 * Game domain does not require transactions. If transactions are needed, do not use these methods!!
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

    public <T> T queryOne(String sql, Class<T> entity, Object id) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            throw new SQLException("sql argument is null");
        }
        if (entity == null) {
            throw new SQLException("entity argument is null");
        }
        if (id == null) {
            throw new SQLException("id argument is null");
        }
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }
        if (bridge.getPrimaryKeyProperties().size() != 1) {
            throw new SQLException(entity.getName() + " queryOne only supports single primary key entity");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    return new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
                }
            }
        } catch (Exception e) {
            logger.error("OrmTemplate queryOne failed", e);
            throw new SQLException(e);
        }
        return null;
    }

    /**
     * Query and return bean entity list
     *
     * @param sql    query statement
     * @param entity entity class
     * @param <T>    entity class
     * @return list of matching instances
     * @throws SQLException sql exception
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> queryMany(String sql, Class<T> entity) throws SQLException {
        List<T> result = new ArrayList<>();
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            Object bean = null;
            while (resultSet.next()) {
                bean = new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
                result.add((T) bean);
            }
        } catch (Exception e) {
            logger.error("OrmTemplate queryMany failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * Query and return a map
     *
     * @param sql query statement
     * @return query result map
     * @throws SQLException sql exception
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
            logger.error("OrmTemplate queryMap failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * Query and return map list
     *
     * @param sql query statement
     * @return query result map list
     * @throws SQLException sql exception
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
            logger.error("OrmTemplate queryMapList failed", e);
            throw new SQLException(e);
        }
        return result;
    }

    /**
     * Execute specific sql statement (prevent sql injection!!!)
     *
     * @param sql sql statement to execute
     * @return execution result
     * @see Statement#execute(String)
     * @throws SQLException sql exception
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
            logger.error("OrmTemplate executeSql failed", e);
            throw new SQLException(e);
        }
    }

    /**
     * Execute update statement (prevent sql injection!!!)
     *
     * @param sql sql statement to execute
     * @return number of affected rows
     * @see Statement#executeUpdate(String)
     * @throws SQLException sql exception
     */
    public int executeUpdate(String sql) throws SQLException {
        if (StringUtil.isEmpty(sql)) {
            return 0;
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            return statement.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("OrmTemplate executeSql failed", e);
            throw new SQLException(e);
        }
    }

    /**
     * Execute insert on entity
     *
     * @param entity entity to insert
     * @return number of affected rows
     * @throws SQLException sql exception
     */
    public int executeInsert(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // Get parameterized SQL
            String sql = SqlFactory.createInsertPreparedSql(bridge);
            // Get parameter values
            List<Object> parameters = SqlParameterUtils.getInsertParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("OrmTemplate executeSql failed", e);
            throw new SQLException(e);
        }
    }

    /**
     * Execute update on entity
     *
     * @param entity entity to update
     * @return number of affected rows
     * @throws SQLException sql exception
     */
    public int executeUpdate(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // Get parameterized SQL
            String sql = SqlFactory.createUpdatePreparedSql(entity, bridge);
            // Get parameter values
            List<Object> parameters = SqlParameterUtils.getUpdateParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("OrmTemplate executeSql failed", e);
            throw new SQLException(e);
        }
    }

    /**
     * Execute delete on entity
     *
     * @param entity entity to delete
     * @return number of affected rows
     * @throws SQLException sql exception
     */
    public int executeDelete(StatefulEntity entity) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            // Get parameterized SQL
            String sql = SqlFactory.createDeletePreparedSql(bridge);
            // Get parameter values
            List<Object> parameters = SqlParameterUtils.getDeleteParameters(entity, bridge);

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // Set parameters
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("OrmTemplate executeSql failed", e);
            throw new SQLException(e);
        }
    }

}
