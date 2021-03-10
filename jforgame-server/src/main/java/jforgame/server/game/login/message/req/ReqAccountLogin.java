package jforgame.server.game.login.message.req;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.server.thread.ThreadCenter;
import jforgame.server.game.Modules;
import jforgame.server.game.login.LoginDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;
import jforgame.socket.actor.MailBox;

/**
 * 请求－账号登录
 * @author kinson
 */
@MessageMeta(module=Modules.LOGIN, cmd=LoginDataPool.REQ_LOGIN)
public class ReqAccountLogin extends Message {
	
	/** 账号流水号 */
	@Protobuf(order = 1)
	private long accountId;
	
	@Protobuf(order = 2)
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

	public MailBox mailQueue() {
		return ThreadCenter.getLoginQueue().getSharedMailQueue(accountId);
	}

	@Override
	public String toString() {
		return "ReqLoginMessage [accountId=" + accountId + ", password="
				+ password + "]";
	}
	
}
