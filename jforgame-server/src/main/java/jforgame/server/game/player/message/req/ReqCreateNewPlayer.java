package jforgame.server.game.player.message.req;

import jforgame.server.game.Modules;
import jforgame.server.game.player.PlayerDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

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
