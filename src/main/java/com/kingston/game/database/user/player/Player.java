package com.kingston.game.database.user.player;

import com.kingston.db.BaseEntity;
import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;
import com.kingston.utils.IdGenerator;

@Entity
public class Player extends BaseEntity<Long>{

	private static final long serialVersionUID = 8913056963732639062L;

	@Id
	@Column
	private long id;
	
	@Column
	private String name;
	
	/**
	 * 职业
	 */
	@Column 
	private int job;
	
	@Column
	private int level;
	
	@Column
	private long exp;
	
	public Player() {
		this.id = IdGenerator.getNextId();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", job=" + job
				+ ", level=" + level + ", exp=" + exp + "]";
	}
	
}
