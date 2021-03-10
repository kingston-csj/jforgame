package jforgame.orm.utils;

import jforgame.orm.OrmBridge;
import jforgame.orm.OrmProcessor;
import jforgame.orm.SqlFactory;
import jforgame.orm.cache.AbstractCacheable;

public class SqlUtils {

	public static String getInsertSql(AbstractCacheable entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createInsertSql(entity, bridge);
	}

	public static String getUpdateSql(AbstractCacheable entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createUpdateSql(entity, bridge);
	}

	public static String getDeleteSql(AbstractCacheable entity) {
		OrmBridge bridge = OrmProcessor.INSTANCE.getOrmBridge(entity.getClass());
		return SqlFactory.createDeleteSql(entity, bridge);
	}
	
	public static String getSaveSql(AbstractCacheable entity) {
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
