package com.kingston.orm.cache;

public abstract class Cacheable {

	/** 当前实体对象的db状态 */
	protected DbStatus status;
	
	public abstract DbStatus getStatus();
	
	public abstract boolean isInsert();
	
	public abstract boolean isUpdate();
	
	public abstract boolean isDelete();
	
	public abstract void setInsert();
	
	public abstract void setUpdate();
	
	public abstract void setDelete();
	
	/**
	 * 进行持久化
	 */
	public abstract void save();
	
}
