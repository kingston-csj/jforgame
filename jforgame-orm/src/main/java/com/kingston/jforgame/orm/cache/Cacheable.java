package com.kingston.jforgame.orm.cache;

public abstract class Cacheable {

	/** 当前实体对象的db状态 */
	protected DbStatus status = DbStatus.NORMAL;
	
	public abstract DbStatus getStatus();
	
	public abstract boolean isInsert();
	
	public abstract boolean isUpdate();
	
	public abstract boolean isDelete();
	
	public abstract void setInsert();
	
	public abstract void setUpdate();
	
	public abstract void setDelete();
	
	/**
	 * 获取持久化对应的sql语句
	 */
	public abstract String getSaveSql();
	
}
