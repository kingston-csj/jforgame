package jforgame.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.orm.utils.DbHelper;

/**
 * 使用proxool数据源对orm-DbUtils的进一步封装
 * @author kinson
 */
public class DbUtils {

	private static Logger logger = LoggerFactory.getLogger(DbUtils.class);

	private static final String PROXOOL = "proxool.";
	/** 策划数据库*/
	public static final String  DB_DATA = PROXOOL + "data";
	/** 玩家数据库 */
	public static final String  DB_USER = PROXOOL + "user";

	public static void init() {
		try {
			logger.info("init database connection pool……");
			JAXPConfigurator.configure("configs/proxool.xml", false);
		} catch (ProxoolException e) {
			logger.error("read config failed ", e);
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
	public static <T> T queryOne(String alias, String sql, Class<?> entity) throws SQLException{
		Connection connection = DbHelper.getConnection(alias);
		return DbHelper.queryOne(connection, sql, entity);
	}
	
	public static <T> T queryOneById(String alias, String sql, Class<?> entity, String id) throws SQLException{
		Connection connection = DbHelper.getConnection(alias);
		return DbHelper.queryOne(connection, sql, entity, id);
	}


	/**
	 * 查询返回bean实体列表
	 * @param alias 数据库别名
	 * @param sql
	 * @param entity
	 * @return
	 */
	public static <T> List<T> queryMany(String alias, String sql, Class<?> entity)throws SQLException {
		Connection connection = DbHelper.getConnection(alias);
		return DbHelper.queryMany(connection, sql, entity);
	}

	/**
	 * 查询返回一个map
	 * @param alias 数据库别名
	 * @param sql
	 * @return
	 */
	public static Map<String, Object> queryMap(String alias, String sql) throws SQLException{
		Connection connection = DbHelper.getConnection(alias);
		return DbHelper.queryMap(connection, sql);
	}

	/**
	 * 查询返回一个map
	 * @param alias 数据库别名
	 * @param sql
	 * @return
	 */
	public static List<Map<String, Object>> queryMapList(String alias, String sql) throws SQLException{
		Connection connection = DbHelper.getConnection(alias);
		return DbHelper.queryMapList(connection, sql);
	}

	/**
	 * 执行特定的sql语句(只有db库有执行权限)
	 * @param sql
	 * @return
	 */
	public static int executeUpdate(String sql) throws SQLException{
		Connection connection = DbHelper.getConnection(DB_USER);
		return DbHelper.executeUpdate(connection, sql);
	}

}
