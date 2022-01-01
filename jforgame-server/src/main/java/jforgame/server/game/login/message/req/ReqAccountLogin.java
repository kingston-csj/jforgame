package jforgame.server.game.login.message.req;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.server.game.Modules;
import jforgame.server.game.login.LoginDataPool;
import jforgame.server.thread.ThreadCenter;
import jforgame.socket.actor.MailBox;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

/**
 * 请求－账号登录
 * @author kinson
 */
@MessageMeta(module=Modules.LOGIN, cmd=LoginDataPool.REQ_LOGIN)
@ProtobufClass
public class ReqAccountLogin implements Message {
	
	/** 账号流水号 */
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

	public MailBox mailQueue() {
		return ThreadCenter.getLoginQueue().getSharedMailQueue(accountId);
	}

	@Override
	public String toString() {
		return "ReqLoginMessage [accountId=" + accountId + ", password="
				+ password + "]";
	}
	
}
