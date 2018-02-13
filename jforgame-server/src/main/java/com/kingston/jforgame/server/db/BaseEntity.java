package com.kingston.jforgame.server.db;

import java.io.Serializable;

import com.kingston.jforgame.orm.cache.AbstractCacheable;

/**
 * abstract base class for db entity
 * @author kingston
 */
public abstract class BaseEntity extends AbstractCacheable
			implements Serializable {

	private static final long serialVersionUID = 5416347850924361417L;

	/**
	 * entity id
	 * @return
	 */
	public abstract long getId() ;
	
	/**
	 * init hook
	 */
	public void doAfterInit() {}
	
	/**
	 * save hook
	 */
	public void doBeforeSave() {}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.valueOf(getId()).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

}
