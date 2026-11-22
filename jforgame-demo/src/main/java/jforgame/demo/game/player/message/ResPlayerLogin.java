package jforgame.demo.game.player.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.player.PlayerDataPool;
import jforgame.socket.core.protocol.annotation.MessageMeta;
import jforgame.socket.core.protocol.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_LOGIN)
public class ResPlayerLogin implements Message {

	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

}
