package com.kingston.jforgame.server.game.cross.ladder.message;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.cross.core.match.AbstractMatchMessage;

public class Req_F2M_HeatBeat extends AbstractMatchMessage  {
	
	private String inetIp;
	
	private int serverId;
	/**
	 * 跨服端口
	 */
	private int port;
	
	public Req_F2M_HeatBeat() {
		ServerConfig  config = ServerConfig.getInstance();
		this.serverId = config.serverId;
		this.inetIp = config.getInetAddr();
		this.port = config.getCrossPort();
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

	public String getInetIp() {
		return inetIp;
	}

	public void setInetIp(String inetIp) {
		this.inetIp = inetIp;
	}
	
}
