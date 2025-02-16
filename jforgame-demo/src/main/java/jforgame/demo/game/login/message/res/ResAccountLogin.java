package jforgame.demo.game.login.message.res;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.demo.game.Modules;
import jforgame.demo.game.login.LoginDataPool;
import jforgame.demo.game.login.message.vo.PlayerLoginVo;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

import java.util.ArrayList;
import java.util.List;

@MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.RES_LOGIN)
@ProtobufClass
public class ResAccountLogin implements Message {

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
