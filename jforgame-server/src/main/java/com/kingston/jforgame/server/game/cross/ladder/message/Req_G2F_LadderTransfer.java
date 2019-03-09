package com.kingston.jforgame.server.game.cross.ladder.message;

import com.kingston.jforgame.socket.message.Message;

public class Req_G2F_LadderTransfer extends Message {
	
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
