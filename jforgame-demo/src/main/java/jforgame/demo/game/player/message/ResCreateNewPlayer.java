package jforgame.demo.game.player.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.player.PlayerDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_CREATE_PLAYER)
public class ResCreateNewPlayer implements Message {

	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

}
