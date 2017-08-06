package com.kingston.game.database.server;

import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;

@Entity
/**
 * 本服公共数据杂项记录
 * @author kingston
 */
public class ServerRecord {
	
	
	/**
	 * 唯一主键
	 */
	@Id
	@Column
	private String key;
	@Id
	private String value;
	
	/**
	 * 注释说明
	 */
	@Id
	private String comment;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
