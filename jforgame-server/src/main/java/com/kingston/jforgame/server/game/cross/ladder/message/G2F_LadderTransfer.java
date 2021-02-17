package com.kingston.jforgame.server.game.cross.ladder.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.cross.core.CrossCommands;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.cross.ladder.service.LadderDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module = Modules.CROSS, cmd = CrossCommands.G2F_LOGIN_TO_SERVER)
public class G2F_LadderTransfer extends Message {

    @Protobuf
    private long playerId;
    /**
     * 跨服登录密钥
     */
    @Protobuf
    private String sign;
    /**
     * 打包后的玩家json数据
     */
    @Protobuf
    private String playerJson;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPlayerJson() {
        return playerJson;
    }

    public void setPlayerJson(String playerJson) {
        this.playerJson = playerJson;
    }

}
