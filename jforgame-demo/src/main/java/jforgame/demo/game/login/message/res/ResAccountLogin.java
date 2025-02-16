package jforgame.demo.game.login.message.res;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import groovy.transform.ToString;
import jforgame.demo.game.Modules;
import jforgame.demo.game.login.LoginDataPool;
import jforgame.demo.game.login.message.vo.PlayerLoginVo;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.socket.share.message.Message;
import jforgame.socket.share.message.MessageFactory;
import jforgame.socket.share.message.SCMessage;

import java.util.ArrayList;
import java.util.List;

//@MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.REQ_LOGIN, isRes = true)
@ProtobufClass
@ToString
public class ResAccountLogin extends SCMessage {

    private List<PlayerLoginVo> players = new ArrayList<>();


    public ResAccountLogin() {
        this.cmd = GameMessageFactory.buildKey(Modules.LOGIN, LoginDataPool.REQ_LOGIN);
    }

    public ResAccountLogin(int code) {
        super(code);
        this.cmd= GameMessageFactory.buildKey(Modules.LOGIN, LoginDataPool.REQ_LOGIN);
    }

    public ResAccountLogin(Message message) {
        super(message);
        this.errorCode = 0;
    }

    public List<PlayerLoginVo> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerLoginVo> players) {
        this.players = players;
    }

}
