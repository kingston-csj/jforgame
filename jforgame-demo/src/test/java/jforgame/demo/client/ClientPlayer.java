package jforgame.demo.client;

import jforgame.commons.JsonUtil;
import jforgame.demo.game.login.message.req.ReqAccountLogin;
import jforgame.demo.game.login.message.req.ReqSelectPlayer;
import jforgame.demo.game.player.message.req.ReqCreateNewPlayer;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.message.Message;

/**
 * 使用socket构建的机器人
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
     *
     * @param message
     */
    public void sendMessage(Message message) {
        System.err.printf("发送请求-->  %s %s%n", message.getClass().getSimpleName(), JsonUtil.object2String(message));
        this.session.send(message);
    }

}
