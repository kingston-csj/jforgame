package com.kingston.db;

import java.io.Serializable;

import com.kingston.orm.cache.AbstractCacheable;

/**
 * db实体基类
 * @author kingston
 */
public abstract class BaseEntity<Id extends Comparable<Id>> extends AbstractCacheable 
			implements Serializable {

	private static final long serialVersionUID = 5416347850924361417L;

	public abstract Id getId() ;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId()==null)?0:getId().hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
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
