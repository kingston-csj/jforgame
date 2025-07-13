package jforgame.orm.utils;

import java.util.List;

import jforgame.orm.OrmBridge;
import jforgame.orm.OrmProcessor;
import jforgame.orm.entity.StatefulEntity;

public class SqlUtils {

	public static String getInsertSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createInsertPreparedSql(bridge);
	}

	public static String getUpdateSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createUpdatePreparedSql(entity, bridge);
	}

	public static String getDeleteSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createDeletePreparedSql(bridge);
	}
	
	public static String getSaveSql(StatefulEntity entity) {
		if (entity.isInsert()) {
			return getInsertSql(entity);
		} else if (entity.isUpdate()) {
			return getUpdateSql(entity);
		} else if (entity.isDelete()) {
			return getDeleteSql(entity);
		}
		return "";
	}

	// ==================== 参数获取方法 ====================

	/**
	 * 获取插入SQL的参数
	 */
	public static List<Object> getInsertParameters(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlParameterUtils.getInsertParameters(entity, bridge);
	}

	/**
	 * 获取更新SQL的参数
	 */
	public static List<Object> getUpdateParameters(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlParameterUtils.getUpdateParameters(entity, bridge);
	}

	/**
	 * 获取删除SQL的参数
	 */
	public static List<Object> getDeleteParameters(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlParameterUtils.getDeleteParameters(entity, bridge);
	}


}
