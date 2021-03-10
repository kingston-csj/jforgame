package jforgame.orm.cache;

public enum DbStatus {

	/** 无需入库 */
	NORMAL,
	/** 需要更新 */
	UPDATE,
	/** 需要插入 */
	INSERT,
	/** 需要删除 */
	DELETE,
	
	;
}
