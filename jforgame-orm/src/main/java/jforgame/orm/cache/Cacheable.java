package jforgame.orm.cache;

public abstract class Cacheable {

	/** 当前实体对象的db状态 */
	protected DbStatus status = DbStatus.NORMAL;

	/**
	 *  返回当前的db状态
	 * @return
	 */
	public abstract DbStatus getStatus();

	/**
	 * 当前是否为插入状态
	 * @return
	 */
	public abstract boolean isInsert();

	/**
	 * 当前是否为更新状态
	 * @return
	 */
	public abstract boolean isUpdate();

	/**
	 * 当前是否为删除状态
	 * @return
	 */
	public abstract boolean isDelete();

	/**
	 * 当前是否为插入状态
	 * @return
	 */
	public abstract void setInsert();

	/**
	 * 切换为更新状态
	 * @return
	 */
	public abstract void setUpdate();

	/**
	 * 切换为删除状态
	 * @return
	 */
	public abstract void setDelete();
	
	/**
	 * 获取持久化对应的sql语句
	 * return {@link String} 入库sql语句
	 */
	public abstract String getSaveSql();
	
}
