package com.kingston.game.database.config.bean;

import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;

@Entity(readOnly = true)
public class ConfigSkill {

	@Id
	@Column
	private int id;

	@Column
	private String name;

	/**
	 * 技能效果说明
	 */
	@Column
	private String effect;

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

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}


}
