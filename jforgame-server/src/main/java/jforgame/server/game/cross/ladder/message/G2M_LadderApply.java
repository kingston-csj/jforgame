package jforgame.server.game.cross.ladder.message;

import jforgame.server.game.Modules;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module = Modules.CROSS, cmd = 9)
public class G2M_LadderApply extends Message {

    private long playerId;
    /**
     * 积分
     */
    private int score;
    /**
     * 战力
     */
    private int power;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "MReqLadderApplyMessage [playerId=" + playerId +
                ", score=" + score + ", power=" + power + "]";
    }

}
