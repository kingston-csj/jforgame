package jforgame.demo.game.player.message.req;

import jforgame.demo.game.Modules;
import jforgame.demo.game.player.PlayerDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.REQ_CREATE_PLAYER)
public class ReqCreateNewPlayer implements Message {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
