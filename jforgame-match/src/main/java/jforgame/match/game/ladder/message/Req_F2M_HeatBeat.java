package jforgame.match.game.ladder.message;

import jforgame.socket.message.Message;

public class Req_F2M_HeatBeat extends Message  {
	
	private String inetIp;

	/**
	 * 游戏服id
	 */
	private int serverId;

	/**
	 * 跨服端口
	 */
	private int port;
	
	public String getInetIp() {
		return inetIp;
	}

	public void setInetIp(String inetIp) {
		this.inetIp = inetIp;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "Req_F2M_HeatBeat [inetIp=" + inetIp + ", serverId=" + serverId + ", port=" + port + "]";
	}
	
}
