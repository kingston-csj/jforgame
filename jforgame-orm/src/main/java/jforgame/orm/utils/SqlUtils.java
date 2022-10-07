package jforgame.orm.utils;

import jforgame.orm.OrmBridge;
import jforgame.orm.OrmProcessor;
import jforgame.orm.StatefulEntity;

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

}
