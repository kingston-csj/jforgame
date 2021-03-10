package jforgame.server.game.login.message.res;

import java.util.ArrayList;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.server.game.Modules;
import jforgame.server.game.login.LoginDataPool;
import jforgame.server.game.login.message.vo.PlayerLoginVo;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.RES_LOGIN)
public class ResAccountLogin extends Message {

	@Protobuf(fieldType = FieldType.OBJECT)
	private List<PlayerLoginVo> players = new ArrayList<>();

	public ResAccountLogin() {

	}

	public List<PlayerLoginVo> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerLoginVo> players) {
		this.players = players;
	}

}
