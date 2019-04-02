package com.kingston.jforgame.server.game.player.message;

import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.player.PlayerDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.REQ_CREATE_PLAYER)
public class ReqCreateNewPlayer extends Message {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
