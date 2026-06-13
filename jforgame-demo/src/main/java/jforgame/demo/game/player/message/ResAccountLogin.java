package jforgame.demo.game.player.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.login.LoginDataPool;
import jforgame.socket.core.protocol.annotation.MessageMeta;
import jforgame.socket.core.protocol.message.Message;

import java.util.ArrayList;
import java.util.List;

@MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.RES_LOGIN)
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
