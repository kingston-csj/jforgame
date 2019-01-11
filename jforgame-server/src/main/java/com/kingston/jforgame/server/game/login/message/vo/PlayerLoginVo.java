package com.kingston.jforgame.server.game.login.message.vo;

public class PlayerLoginVo {
	
	private long id;
	
	private String name;
	/** 角色战力 */
	private long fighting;
	public long getId() {
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
	public long getFighting() {
		return fighting;
	}
	public void setFighting(long fighting) {
		this.fighting = fighting;
	}
	
	@Override
	public String toString() {
		return "PlayerLoginVo [id=" + id + ", name=" + name + ", fighting=" + fighting + "]";
	}

}
