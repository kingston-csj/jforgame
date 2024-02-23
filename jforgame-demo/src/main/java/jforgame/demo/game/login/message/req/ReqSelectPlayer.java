package jforgame.demo.game.login.message.req;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.demo.game.Modules;
import jforgame.demo.game.login.LoginDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

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
