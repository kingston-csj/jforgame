package jforgame.server;

import java.net.SocketException;

import jforgame.server.utils.XmlUtils;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.common.utils.IpAddrUtil;

@Root(name = "server")
public class ServerConfig {

	private Logger logger = LoggerFactory.getLogger(ServerConfig.class.getSimpleName());

	private static volatile ServerConfig instance;

	/** 内網ip地址 */
	private String inetAddr;
	/** 服务器id */
	@Element(required = true)
	private int serverId;
	/** 服务器端口 */
	@Element(required = true)
	private int serverPort;
	/** 客户端封包最大字节数 */
	@Element(required = true)
	private int maxReceiveBytes;

	/** 后台管理端口 */
	@Element(required = true)
	private int adminPort;
	/** 后台白名单模式 */
	@Element(required = true)
	private String whiteIps;

	private String[] whiteIpPattern;

	/** 匹配服http地址 */
	@Element(required = true)
	private String matchUrl;
	/** 本服是否為跨服 */
	@Element(required = true)
	private boolean fight;
	/** 对外跨服端口 */
	@Element(required = true)
	private int crossPort;

	/** redis server url {http:port} */
	@Element(required = true)
	private String redisUrl;

	private ServerConfig() {
	}

	public static ServerConfig getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (ServerConfig.class) {
			if (instance == null) {
				instance = XmlUtils.loadXmlConfig("server.xml", ServerConfig.class);
				instance.init();
			}
		}
		return instance;
	}

	private void init() {
		String[] ips = whiteIps.split(";");
		this.whiteIpPattern = new String[ips.length];
		for (int i = 0; i < ips.length; i++) {
			this.whiteIpPattern[i] = ips[i];
		}

		try {
			this.inetAddr = IpAddrUtil.getInnetIp();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		logger.info("本服serverId为{},后台端口为{}", this.serverId, this.adminPort);
	}

	public String getInetAddr() {
		return inetAddr;
	}

	public void setInetAddr(String inetAddr) {
		this.inetAddr = inetAddr;
	}

	public int getServerId() {
		return this.serverId;
	}

	public int getServerPort() {
		return this.serverPort;
	}

	public int getHttpPort() {
		return adminPort;
	}

	public String[] getWhiteIpPattern() {
		return whiteIpPattern;
	}

	public void setWhiteIpPattern(String[] whiteIpPattern) {
		this.whiteIpPattern = whiteIpPattern;
	}

	public String getRedisUrl() {
		return redisUrl;
	}

	public String getMatchUrl() {
		return matchUrl;
	}

	public boolean isFight() {
		return fight;
	}

	public int getCrossPort() {
		return crossPort;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setFight(boolean fight) {
		this.fight = fight;
	}

	public void setCrossPort(int crossPort) {
		this.crossPort = crossPort;
	}

	public int getMaxReceiveBytes() {
		return maxReceiveBytes;
	}

	public void setMaxReceiveBytes(int maxReceiveBytes) {
		this.maxReceiveBytes = maxReceiveBytes;
	}
}
