package jforgame.server.client;

import com.google.gson.Gson;
import jforgame.server.game.login.message.req.ReqAccountLogin;
import jforgame.server.game.login.message.req.ReqSelectPlayer;
import jforgame.server.game.player.message.req.ReqCreateNewPlayer;
import jforgame.server.logs.LoggerUtils;
import jforgame.socket.IdSession;
import jforgame.socket.message.Message;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * 使用socket构建的机器人
 *
 */
public class ClientPlayer {

	private String name;

	private IdSession session;

	public ClientPlayer(IdSession session) {
		this.session = session;
	}

	public void createNew() {
		ReqCreateNewPlayer req = new ReqCreateNewPlayer();
		req.setName("Happy");
		this.sendMessage(req);
	}

	public void login() {
		ReqAccountLogin request = new ReqAccountLogin();
		request.setPassword("admin");
		request.setAccountId(123L);
		this.sendMessage(request);
	}


	public void selectedPlayer(long playerId) {
		ReqSelectPlayer request = new ReqSelectPlayer();
		request.setPlayerId(playerId);
		this.sendMessage(request);
	}

	/**
	 * 发送消息
	 * @param message
	 */
	public void sendMessage(Message message) {
		System.err.println(String.format("发送请求-->  %s %s", message.getClass().getSimpleName(), new Gson().toJson(message)));
		this.session.sendPacket(message);
	}

	private class ClientHandler extends IoHandlerAdapter {

		@Override
		public void messageReceived(IoSession session, Object message) {
			System.err.println(String.format("收到响应<--  %s %s", message.getClass().getSimpleName(), new Gson().toJson(message)));
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			LoggerUtils.error("client exception", cause);
		}
	}

}
