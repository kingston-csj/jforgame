package jforgame.match.game.ladder.model;

/**
 * 跨服节点信息
 *
 */
public class FightServerNode {
	
	/**
	 * 战斗服id
	 */
	private int serverId;
	
	/**
	 * 战斗服ip地址
	 */
	private String ip;

	/**
	 * 战斗服端口
	 */
	private int port;
	
	/**
	 * 上一次心跳包的时间戳
	 */
	private long lastHeatBeat;
	

	public FightServerNode(int serverId, String ip, int port) {
		super();
		this.serverId = serverId;
		this.ip = ip;
		this.port = port;
		this.lastHeatBeat = System.currentTimeMillis();
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public long getLastHeatBeat() {
		return lastHeatBeat;
	}

	public void updateLastHeatBeat() {
		this.lastHeatBeat = System.currentTimeMillis();
	}
	
	/**
	 * XX时间没收到心跳包，则认为链接断了
	 * @return
	 */
	public boolean isOverTime() {
		long escaped = System.currentTimeMillis() - lastHeatBeat;
		return escaped > 60 * 1000;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + serverId;
		return result;
	}

	/**
	 * serverId一致的即可认为是同一个服务
	 * ip地址或端口都有可能变化
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FightServerNode other = (FightServerNode) obj;
		if (serverId != other.serverId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FightServerNode [serverId=" + serverId + ", ip=" + ip + ", port=" + port + "]";
	}

}
