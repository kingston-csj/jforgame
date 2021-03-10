package jforgame.server.game.database.config.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import jforgame.server.game.function.model.OpenType;

@Entity
public class ConfigFunction {

	/** 功能id */
	@Id
	@Column
	private int id;
	@Column
	private String name;
	@Column
	private OpenType openType;
	/** 开启的条件值 */
	@Column
	private int openTarget;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public OpenType getOpenType() {
		return openType;
	}
	public void setOpenType(OpenType openType) {
		this.openType = openType;
	}
	public int getOpenTarget() {
		return openTarget;
	}
	public void setOpenTarget(int openTarget) {
		this.openTarget = openTarget;
	}

}
