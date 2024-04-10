package jforgame.demo.udp;

import jforgame.socket.share.HostAndPort;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.annotation.MessageRoute;
import jforgame.socket.share.annotation.RequestHandler;

@MessageRoute
public class LoginRouter {

    @RequestHandler
    public void reqTime(IdSession session, ReqLogin req) {
        long playerId = req.getPlayerId();
        System.out.println("player login" + playerId);
        Player player = new Player();
        player.setId(playerId);
        player.setRemoteAddr(HostAndPort.valueOf(req.getSenderIp(), req.getSenderPort()));
        SessionManager.getInstance().register(playerId, player);
        ResPlayerLogin resp = new ResPlayerLogin();
        resp.setPlayerId(playerId);
        player.receive(session, resp);
    }
}
