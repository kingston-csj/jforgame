package com.kingston.orm.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.orm.utils.DbHelper;
import com.kingston.orm.utils.SqlUtils;

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
	
	public final void resetDbStatus() {
		this.status = DbStatus.NORMAL;
	}
	
	public final String getSaveSql() {
		return SqlUtils.getSaveSql(this);
//		if (DbUtils.executeSql(saveSql)) {
//			this.status = DbStatus.NORMAL;
//		}
//		if (logger.isDebugEnabled()) {
//			System.err.println(saveSql);
//		}
	}
}
