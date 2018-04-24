package com.kingston.jforgame.orm.cache;

import com.kingston.jforgame.orm.utils.SqlUtils;

public abstract class AbstractCacheable extends Cacheable {

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

	@Override
	public void setInsert() {
		this.status = DbStatus.INSERT;
	}

	@Override
	public final void setUpdate(){
		//只有该状态才可以变更为update
		if (this.status == DbStatus.NORMAL) {
			this.status = DbStatus.UPDATE;
		}
	}

	@Override
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

	@Override
	public final String getSaveSql() {
		return SqlUtils.getSaveSql(this);
	}
}
