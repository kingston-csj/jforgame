package jforgame.demo.udp;

import jforgame.socket.core.net.HostAndPort;
import jforgame.socket.core.session.IdSession;
import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;

@MessageRoute
public class LoginRouter {

    @RequestHandler
    public void reqLogin(IdSession session, ReqLogin req) {
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
