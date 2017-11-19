package com.kingston.jforgame.server.game.database.config.bean;

import com.kingston.jforgame.server.game.activity.ActivityTypes;
import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;

@Entity(readOnly = true)
public class ConfigActivity {

	@Column
	@Id
	private int id;
	/** 活动类型 {@link ActivityTypes#getType()} */
	@Column
	private int type;
	/** 活动名称 */
	@Column
	private String name;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}



}
