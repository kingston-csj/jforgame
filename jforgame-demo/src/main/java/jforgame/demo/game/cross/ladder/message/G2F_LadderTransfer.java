package jforgame.demo.game.cross.ladder.message;

import jforgame.demo.cross.core.CrossCommands;
import jforgame.demo.game.Modules;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.CROSS, cmd = CrossCommands.G2F_LOGIN_TO_SERVER)
public class G2F_LadderTransfer implements Message {

    private long playerId;
    /**
     * 跨服登录密钥
     */
    private String sign;
    /**
     * 打包后的玩家json数据
     */
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
