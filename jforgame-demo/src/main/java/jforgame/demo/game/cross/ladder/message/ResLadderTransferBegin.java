package jforgame.demo.game.cross.ladder.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.cross.ladder.service.LadderDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

/**
 * 通知客户端切换socket到战斗服
 *
 */
@MessageMeta(module = Modules.CROSS_BUSINESS, cmd = LadderDataPool.RES_LADDER_TRANSFER_BEGIN)
public class ResLadderTransferBegin implements Message {
	
	/**
	 * 传输密钥（战斗服登录密码）
	 */
	private String sign;

	/**
	 * 战斗服ip地址
	 */
	private String targetIp;
	
	/**
	 * 战斗服端口
	 */
	private int targetPort;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTargetIp() {
		return targetIp;
	}

	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}
	
}
