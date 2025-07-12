package jforgame.orm.utils;

import java.util.List;

import jforgame.orm.OrmBridge;
import jforgame.orm.OrmProcessor;
import jforgame.orm.entity.StatefulEntity;

public class SqlUtils {

	public static String getInsertSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createInsertSql(entity, bridge);
	}

	public static String getUpdateSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createUpdateSql(entity, bridge);
	}

	public static String getDeleteSql(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createDeleteSql(entity, bridge);
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

	/**
	 * 根据实体状态获取对应的参数
	 */
	public static List<Object> getSaveParameters(StatefulEntity entity) {
		if (entity.isInsert()) {
			return getInsertParameters(entity);
		} else if (entity.isUpdate()) {
			return getUpdateParameters(entity);
		} else if (entity.isDelete()) {
			return getDeleteParameters(entity);
		}
		return new java.util.ArrayList<>();
	}

	// ==================== 兼容性方法（字符串拼接版本） ====================

	/**
	 * 获取插入SQL（字符串拼接版本）
	 * @deprecated 使用getInsertSql替代，避免SQL注入风险
	 */
	@Deprecated
	public static String getInsertSqlString(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createInsertSqlString(entity, bridge);
	}

	/**
	 * 获取更新SQL（字符串拼接版本）
	 * @deprecated 使用getUpdateSql替代，避免SQL注入风险
	 */
	@Deprecated
	public static String getUpdateSqlString(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createUpdateSqlString(entity, bridge);
	}

	/**
	 * 获取删除SQL（字符串拼接版本）
	 * @deprecated 使用getDeleteSql替代，避免SQL注入风险
	 */
	@Deprecated
	public static String getDeleteSqlString(StatefulEntity entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createDeleteSqlString(entity, bridge);
	}

	/**
	 * 获取保存SQL（字符串拼接版本）
	 * @deprecated 使用getSaveSql替代，避免SQL注入风险
	 */
	@Deprecated
	public static String getSaveSqlString(StatefulEntity entity) {
		if (entity.isInsert()) {
			return getInsertSqlString(entity);
		} else if (entity.isUpdate()) {
			return getUpdateSqlString(entity);
		} else if (entity.isDelete()) {
			return getDeleteSqlString(entity);
		}
		return "";
	}
}
