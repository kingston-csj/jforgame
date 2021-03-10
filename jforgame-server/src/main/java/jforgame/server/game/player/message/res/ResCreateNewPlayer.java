package jforgame.server.game.player.message.res;

import jforgame.server.game.Modules;
import jforgame.server.game.player.PlayerDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_CREATE_PLAYER)
public class ResCreateNewPlayer extends Message {

	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

}
