package jforgame.orm.entity;

import jforgame.orm.DbStatus;

/**
 * 拥有各种db状态的对象
 */
public abstract class Stateful {

	/** 当前实体对象的db状态 */
	protected DbStatus status = DbStatus.NORMAL;

	/**
	 *  返回当前的db状态
	 * @return 前的db状态
	 */
	public abstract DbStatus getStatus();

	/**
	 * 当前是否为插入状态
	 * @return 当前是否为插入状态
	 */
	public abstract boolean isInsert();

	/**
	 * 当前是否为更新状态
	 * @return 当前是否为更新状态
	 */
	public abstract boolean isUpdate();

	/**
	 * 当前是否为删除状态
	 * @return 当前是否为删除状态
	 */
	public abstract boolean isDelete();

	/**
	 * 当前是否为插入状态
	 */
	public abstract void setInsert();

	/**
	 * 切换为更新状态
	 */
	public abstract void setUpdate();

	/**
	 * 切换为删除状态
	 */
	public abstract void setDelete();
	

}
