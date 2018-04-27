package com.kingston.jforgame.admin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kingston
 */
public class DbHelper {

	private static Logger logger = LoggerFactory.getLogger(DbHelper.class);

	public static final String GM = "proxool.gm";
	public static final String LOGIN = "proxool.login";

	public static void init(String dbPath) {
		try {
			logger.info("初始化数据库连接池……");
			JAXPConfigurator.configure(dbPath, true);
		} catch (ProxoolException e) {
			e.printStackTrace();
			logger.error("读取数据库配置文件出错 " + e.getMessage(), e);
		}
	}

	public static Connection getConn(String alias) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(alias);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return conn;
	}

	/**
	 * 执行查询 ，将每行的结果集放到map中
	 */
	public static Map<String, String> queryMap(String alias, String sql) {
		Statement stmt = null;
		ResultSet rst = null;
		Connection con = getConn(alias);
		ResultSetHandler h = new MapHandler();
		try {
			stmt = con.createStatement();
			rst = stmt.executeQuery(sql);
			return (Map) h.handle(rst);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if (rst != null) {
					rst.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return null;
	}

	public static List<Map<String, String>> queryMapList(String alias, String sql) {
		Statement stmt = null;
		ResultSet rst = null;
		Connection con = getConn(alias);
		ResultSetHandler h = new MapListHandler();
		try {
			stmt = con.createStatement();
			rst = stmt.executeQuery(sql);
			return (List<Map<String, String>>) h.handle(rst);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if (rst != null) {
					rst.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return null;
	}



}
