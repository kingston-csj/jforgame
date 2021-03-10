package com.kinson.orm.entity;

import jforgame.orm.cache.AbstractCacheable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="player")
public class User extends AbstractCacheable {

	@Column(name="id")
	@Id
	private long no;
	@Column
	private String name;

	public long getNo() {
		return no;
	}

	public void setNo(long id) {
		this.no = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Player [id=" + no + ", name=" + name + "]";
	}

}
