package jforgame.demo.game.player.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.player.PlayerDataPool;
import jforgame.socket.core.protocol.annotation.MessageMeta;
import jforgame.socket.core.protocol.message.Message;

/**
 * 请求－账号登录
 */
@MessageMeta(module = Modules.LOGIN, cmd = PlayerDataPool.REQ_LOGIN)
public class ReqPlayerLogin implements Message {

    /**
     * 账号流水号
     */
    private long playerId;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }


}
