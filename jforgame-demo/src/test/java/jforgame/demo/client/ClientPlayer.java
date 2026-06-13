package jforgame.demo.client;

import jforgame.commons.util.JsonUtil;
import jforgame.demo.game.player.message.ReqAccountLogin;
import jforgame.socket.core.protocol.message.Message;
import jforgame.socket.core.session.IdSession;

/**
 * 使用socket构建的机器人
 */
public class ClientPlayer {

    private String name;

    private IdSession session;

    public ClientPlayer(IdSession session) {
        this.session = session;
    }

    public void login() {
        ReqAccountLogin request = new ReqAccountLogin();
        request.setPlayerId(123L);
        this.sendMessage(request);
    }


    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(Message message) {
        System.err.printf("发送请求-->  %s %s%n", message.getClass().getSimpleName(), JsonUtil.object2String(message));
        this.session.send(message);
    }

}
