package com.kingston.orm.cache;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.orm.utils.DbUtils;
import com.kingston.orm.utils.SqlUtils;

/**
 * This class provides a skeletal implementation of the <tt>Cacheable</tt> interface
 * @author kingston
 */
public abstract class AbstractCacheable extends Cacheable {

	private static Logger logger = LoggerFactory.getLogger(AbstractCacheable.class);

	@Override
	public DbStatus getStatus() {
		return this.status;
	}

	@Override
	public final boolean isInsert() {
		return this.status == DbStatus.INSERT;
	}

	@Override
	public final boolean isUpdate() {
		return this.status == DbStatus.UPDATE;
	}

	@Override
	public final boolean isDelete() {
		return this.status == DbStatus.DELETE;
	}

	public void setInsert() {
		this.status = DbStatus.INSERT;
	}

	public final void setUpdate(){
		//只有该状态才可以变更为update
		if (this.status == DbStatus.NORMAL) {
			this.status = DbStatus.UPDATE;
		}
	}

	public final void setDelete(){
		if (this.status == DbStatus.INSERT) {
			this.status = DbStatus.NORMAL;
		} else{
			this.status = DbStatus.DELETE;
		}
	}

	public final void save() {
		String saveSql = SqlUtils.getSaveSql(this);
		if (StringUtils.isBlank(saveSql)) {
			return;
		}
		if (DbUtils.executeSql(saveSql)) {
			this.status = DbStatus.NORMAL;
		}
		if (logger.isDebugEnabled()) {
			System.err.println(saveSql);
		}
	}
}
