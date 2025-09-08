package jforgame.demo.game.login.message.req;

import jforgame.demo.game.Modules;
import jforgame.demo.game.login.LoginDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

/**
 * 请求－账号登录
 */
@MessageMeta(module = Modules.LOGIN, cmd = LoginDataPool.REQ_LOGIN)
public class ReqAccountLogin implements Message {

    /**
     * 账号流水号
     */
    private long accountId;

    private String password;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long playerId) {
        this.accountId = playerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ReqLoginMessage [accountId=" + accountId + ", password="
                + password + "]";
    }

}
