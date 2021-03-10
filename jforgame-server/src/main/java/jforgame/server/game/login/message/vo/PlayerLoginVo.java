package jforgame.server.game.login.message.vo;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class PlayerLoginVo {
	
	@Protobuf
	private long id;
	@Protobuf
	private String name;
	/** 角色战力 */
	@Protobuf
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
