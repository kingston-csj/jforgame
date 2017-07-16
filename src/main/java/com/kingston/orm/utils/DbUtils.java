package com.kingston.orm.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.logs.LoggerUtils;
import com.kingston.orm.BeanProcessor;
import com.kingston.orm.OrmBridge;
import com.kingston.orm.OrmProcessor;

public class DbUtils {

	private static Logger logger = LoggerFactory.getLogger(DbUtils.class); 

	private static final String PROXOOL = "proxool.";
	//策划数据库
	public static final String  DB_DATA = PROXOOL + "data";
	//人物数据库
	public static final String  DB_USER = PROXOOL + "user";

	public static void init() {
		try {
			logger.info("初始化数据库连接池……");
			// 读取配置文件并创建数据库连接池
			JAXPConfigurator.configure("configs/proxool.xml", true);
		} catch (ProxoolException e) {
			LoggerUtils.error("读取数据库配置文件出错", e);
			System.exit(-1);
		}
	}

	/**
	 * 查询返回一个bean实体
	 * @param alias 数据库别名
	 * @param sql
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T queryOne(String alias, String sql, Class<?> entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity);
		if (bridge == null || entity == null || StringUtils.isEmpty(sql)) {
			return null;
		}
		Connection connection = null;
		Statement statement = null;
		try{
			connection = getConnection(alias);
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);	
			while (resultSet.next()) {
				return  (T) new BeanProcessor(bridge.getColumnToPropertyOverride()).toBean(resultSet, entity);
			}
		}catch(Exception e) {
			logger.error("DbUtils queryOne failed", e);
		}finally {
			if (connection != null) {
				try{
					connection.close();
				}catch(Exception e2) {
					logger.error("DbUtils queryOne failed", e2);
				}
			}
		}
		return null;
	}

	/**
	 * 查询返回bean实体列表
	 * @param alias 数据库别名
	 * @param sql
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> queryMany(String alias, String sql, Class<?> entity) {
		List<T> result = new ArrayList<>();
		Connection connection = null;
		Statement statement = null;
		try{
			connection = getConnection(alias);
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);	
			Object bean = entity.newInstance();
			while (resultSet.next()) {
				bean = new BeanProcessor().toBean(resultSet, entity);
				result.add((T) bean);
			}
		}catch(Exception e) {
			logger.error("DbUtils queryMany failed", e);
		}finally {
			if (connection != null) {
				try{
					connection.close();
				}catch(Exception e2) {
					logger.error("DbUtils queryMany failed", e2);
				}
			}
		}
		return result;
	}

	/**
	 * 查询返回一个map
	 * @param alias 数据库别名
	 * @param sql
	 * @param entity
	 * @return
	 */
	public static Map<String, Object> queryMap(String alias, String sql) {
		Connection connection = null;
		Statement statement = null;
		Map<String, Object> result = new HashMap<>();
		try{
			connection = getConnection(alias);
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);	
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				int cols = rsmd.getColumnCount();
				for (int i = 1; i <= cols; i++)
				{
					String columnName = rsmd.getColumnLabel(i);
					if ((null == columnName) || (0 == columnName.length())) {
						columnName = rsmd.getColumnName(i);
					}
					result.put(columnName, rs.getObject(i));
				}
				break;
			}
		}catch(Exception e) {
			logger.error("DbUtils queryMap failed", e);
		}finally {
			if (connection != null) {
				try{
					connection.close();
				}catch(Exception e2) {
					logger.error("DbUtils queryMap failed", e2);
				}
			}
		}
		return result;
	}

	/**
	 * 查询返回一个map
	 * @param alias 数据库别名
	 * @param sql
	 * @param entity
	 * @return
	 */
	public static List<Map<String, Object>> queryMapList(String alias, String sql) {
		Connection connection = null;
		Statement statement = null;
		List<Map<String, Object>> result = new ArrayList<>();
		try{
			connection = getConnection(alias);
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);	
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				int cols = rsmd.getColumnCount();
				Map<String, Object> map = new HashMap<>();
				for (int i = 1; i <= cols; i++)
				{
					String columnName = rsmd.getColumnLabel(i);
					if ((null == columnName) || (0 == columnName.length())) {
						columnName = rsmd.getColumnName(i);
					}
					map.put(columnName, rs.getObject(i));
				}
				result.add(map);
			}
		}catch(Exception e) {
			logger.error("DbUtils queryMapList failed", e);
		}finally {
			if (connection != null) {
				try{
					connection.close();
				}catch(Exception e2) {
					logger.error("DbUtils queryMapList failed", e2);
				}
			}
		}
		return result;
	}

	/**
	 * 执行特定的sql语句(只有db库有执行权限)
	 * @param sql
	 * @return
	 */
	public static boolean executeSql(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return true;
		}
		Connection connection = null;
		Statement statement = null;
		try{
			connection = getConnection(DB_USER);
			statement = connection.createStatement();
			statement.execute(sql);	
			return true;
		}catch (Exception e) {
			logger.error("DbUtils executeSql failed", e);
		}finally {
			if (connection != null) {
				try{
					connection.close();
				}catch(Exception e2) {
					logger.error("DbUtils executeSql failed", e2);
				}
			}
		}
		return false;
	}

	/**
	 * 获得连接
	 *
	 * @param alias
	 * @return
	 */
	public static Connection getConnection(String alias) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(alias);
		} catch (SQLException e) {
			LoggerUtils.error("getConn", e);
		}
		return conn;
	}

	/**
	 * 关闭连接
	 *
	 * @param conn
	 */
	public static void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LoggerUtils.error("closeCon", e);
		}
	}

	/**
	 * 关闭statement
	 *
	 * @param st
	 */
	public static void closeStatement(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (SQLException e) {
			LoggerUtils.error("closeStatement", e);
		}
	}

	/**
	 * 关闭resultSet
	 *
	 * @param rst
	 */
	public static void closeResultSet(ResultSet rst) {
		try {
			if (rst != null) {
				rst.close();
			}
		} catch (SQLException e) {
			LoggerUtils.error("closeResultSet", e);
		}
	}

}
