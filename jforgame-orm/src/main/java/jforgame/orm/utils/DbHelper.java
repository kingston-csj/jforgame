package jforgame.orm.utils;

import jforgame.orm.BeanProcessor;
import jforgame.orm.FieldMetadata;
import jforgame.orm.OrmBridge;
import jforgame.orm.OrmProcessor;
import jforgame.orm.StatefulEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbHelper {

    private static Logger logger = LoggerFactory.getLogger(DbHelper.class);

    /**
     * 查询返回一个bean实体
     *
     * @param connection 数据库链接
     * @param sql
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T queryOne(Connection connection, String sql, Class<?> entity) throws SQLException {
        if (StringUtils.isEmpty(sql)) {
            throw new SQLException("sql argument is null");
        }
        if (entity == null) {
            throw new SQLException("entity argument is null");
        }
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                return (T) new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryOne failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
        return null;
    }

    public static <T> T queryOne(Connection connection, String sql, Class<?> entity, String id) throws SQLException {
        if (StringUtils.isEmpty(sql)) {
            throw new SQLException("sql argument is null");
        }
        if (entity == null) {
            throw new SQLException("entity argument is null");
        }
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
        if (bridge == null) {
            throw new SQLException(entity.getName() + " bridge is null");
        }
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return (T) new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryOne failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
        return null;
    }

    /**
     * 查询返回bean实体列表
     *
     * @param connection 数据库链接
     * @param sql
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> queryMany(Connection connection, String sql, Class<?> entity) throws SQLException {
        List<T> result = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            Object bean = entity.newInstance();
            while (resultSet.next()) {
                bean = new BeanProcessor().toBean(resultSet, entity);
                result.add((T) bean);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMany failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
        return result;
    }

    /**
     * 查询返回一个map
     *
     * @param connection 数据库链接
     * @param sql
     * @return
     */
    public static Map<String, Object> queryMap(Connection connection, String sql) throws SQLException {
        Statement statement = null;
        Map<String, Object> result = new HashMap<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                int cols = rsmd.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if ((null == columnName) || (0 == columnName.length())) {
                        columnName = rsmd.getColumnName(i);
                    }
                    result.put(columnName, rs.getObject(i));
                }
                break;
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMap failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
        return result;
    }

    /**
     * 查询返回一个map
     *
     * @param connection 数据库链接
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> queryMapList(Connection connection, String sql) throws SQLException {
        Statement statement = null;
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                int cols = rsmd.getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    if ((null == columnName) || (0 == columnName.length())) {
                        columnName = rsmd.getColumnName(i);
                    }
                    map.put(columnName, rs.getObject(i));
                }
                result.add(map);
            }
        } catch (Exception e) {
            logger.error("DbUtils queryMapList failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
        return result;
    }

    /**
     * 执行特定的sql语句（防止sql注入!!!）
     *
     * @param connection 数据库链接
     * @param sql
     * @return
     * @see Statement#execute(String)
     */
    public static boolean executeSql(Connection connection, String sql) throws SQLException {
        if (StringUtils.isEmpty(sql)) {
            return true;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sql);
            return true;
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
    }

    /**
     * 执行update语句（防止sql注入!!!）
     *
     * @param connection 数据库链接
     * @param sql
     * @return
     * @see Statement#executeUpdate(String)
     */
    public static int executeUpdate(Connection connection, String sql) throws SQLException {
        if (StringUtils.isEmpty(sql)) {
            return 0;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
    }

    public static int executeInsert(Connection connection, StatefulEntity entity) throws SQLException {
        PreparedStatement statement = null;
        try {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            String sql = SqlFactory.createPreparedInsertSql(entity, bridge);
            statement = connection.prepareStatement(sql);
            ParameterMetaData pmd = statement.getParameterMetaData();
            List<String> properties = bridge.listProperties();
            for (int i = 0; i < properties.size(); i++) {
                String property = properties.get(i);
                FieldMetadata fieldMetadata = bridge.getFieldMetadataMap().get(property);
                int  parameterIndex = i+1;
                try {
                    Object value = fieldMetadata.getField().get(entity);
                    if (fieldMetadata.getConverter() != null) {
                        // 进行转换
                        value = fieldMetadata.getConverter().convertToDatabaseColumn(value);
                    }
                    if (value != null) {
                        if (value.getClass().isEnum()) {
                            statement.setObject(parameterIndex, value.toString());
                        } else {
                            statement.setObject(parameterIndex, value);
                        }
                    } else {
                        int sqlType = pmd.getParameterType(parameterIndex);
                        statement.setNull(parameterIndex, sqlType);
                    }
                } catch (Exception e) {
                    logger.error("createInsertSql failed", e);
                }
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
    }

    public static int executeUpdate(Connection connection, StatefulEntity entity) throws SQLException {
        PreparedStatement statement = null;
        ParameterMetaData pmd = null;
        try {
            OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
            LinkedHashMap<String, Object> column2Value = changedFieldValue(entity);
            String sql = SqlFactory.createPreparedUpdateSql(entity, bridge, column2Value.keySet().toArray());
            statement = connection.prepareStatement(sql);
            pmd = statement.getParameterMetaData();

            int i = 1;
            for (Map.Entry<String, Object> entry : column2Value.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    if (value.getClass().isEnum()) {
                        statement.setObject(i, value.toString());
                    } else {
                        statement.setObject(i, value);
                    }
                } else {
                    int sqlType = pmd.getParameterType(i);
                    statement.setNull(i, sqlType);
                }
                i++;
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            logger.error("DbUtils executeSql failed", e);
            throw new SQLException(e);
        } finally {
            if (connection != null) {
                closeConn(connection);
            }
        }
    }

    private static LinkedHashMap<String, Object> changedFieldValue(StatefulEntity entity) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
        Set<String> columns = entity.savingColumns();
        StringBuilder sb = new StringBuilder();
        boolean saveAll = entity.isSaveAll() || columns == null || columns.size() <= 0;
        for (Map.Entry<String, FieldMetadata> entry : bridge.getFieldMetadataMap().entrySet()) {
            String property = entry.getKey();
            // 仅持久化部分字段
            if (!saveAll && !columns.contains(property)) {
                continue;
            }
            FieldMetadata metadata = entry.getValue();
            try {
                Object value = metadata.getField().get(entity);
                if (metadata.getConverter() != null) {
                    // 进行转换
                    value = metadata.getConverter().convertToDatabaseColumn(value);
                }
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                String column = entry.getKey();
                if (bridge.getOverrideProperty(property) != null) {
                    column = bridge.getOverrideProperty(property);
                }
                result.put(column, value);
            } catch (Exception e) {
                logger.error("object2SetterSql failed", e);
            }
        }

        return result;
    }

    /**
     * 关闭连接
     *
     * @param conn
     */
    public static void closeConn(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * 关闭statement
     *
     * @param st
     * @throws SQLException
     */
    public static void closeStatement(Statement st) throws SQLException {
        if (st != null) {
            st.close();
        }
    }

    /**
     * 关闭resultSet
     *
     * @param rst
     */
    public static void closeResultSet(ResultSet rst) throws SQLException {
        if (rst != null) {
            rst.close();
        }
    }

}
