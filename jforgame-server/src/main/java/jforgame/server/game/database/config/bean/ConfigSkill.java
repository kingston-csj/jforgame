package jforgame.server.game.database.config.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity()
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
