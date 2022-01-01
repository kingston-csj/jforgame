package jforgame.server.game.login.message.req;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.server.game.Modules;
import jforgame.server.game.login.LoginDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.LOGIN, cmd=LoginDataPool.REQ_SELECT_PLAYER)
@ProtobufClass
public class ReqSelectPlayer implements Message {
	
	private long playerId;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public String toString() {
		return "ReqSelectPlayerMessage [playerId=" + playerId + "]";
	}
	
}
