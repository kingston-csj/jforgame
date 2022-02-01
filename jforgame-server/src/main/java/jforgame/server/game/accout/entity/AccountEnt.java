package jforgame.server.game.accout.entity;

import jforgame.server.db.BaseEntity;
import jforgame.server.utils.IdGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class AccountEnt extends BaseEntity<Long> {
	
	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	public AccountEnt() {
		this.id = IdGenerator.getNextId();
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
