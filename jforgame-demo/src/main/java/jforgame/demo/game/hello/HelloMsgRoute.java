package jforgame.demo.game.hello;

import jforgame.socket.session.IdSession;
import jforgame.socket.protocol.annotation.MessageRoute;
import jforgame.socket.protocol.annotation.RequestHandler;

@MessageRoute
public class HelloMsgRoute {

    @RequestHandler
    public Object sayHello(IdSession session, int index, ReqHello request) {
        ResHello response = new ResHello();
        response.setContent("hello, rpc, index = " + index);
        return response;
    }
}
