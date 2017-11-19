package com.kingston.orm.entity;

import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;
import com.kingston.orm.cache.AbstractCacheable;

@Entity(table="player")
public class Player extends AbstractCacheable {

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
