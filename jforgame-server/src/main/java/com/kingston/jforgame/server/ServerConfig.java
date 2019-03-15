package com.kingston.jforgame.server;

import java.net.SocketException;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kingston.jforgame.common.utils.IpAddrUtil;
import com.kingston.jforgame.common.utils.NumberUtil;
import com.kingston.jforgame.server.utils.XmlUtils;
import com.kingston.jforgame.socket.GateServerConfig;

public class ServerConfig {

	private Logger logger = LoggerFactory.getLogger(ServerConfig.class.getSimpleName());

	private static ServerConfig instance = new ServerConfig();

	/** 服务器id */
	public int serverId;
	/** 内網ip地址 */
	private String inetAddr;
	/** 服务器端口 */
	public int serverPort;
	/** 匹配服http地址 */
	private String matchUrl;
	/** 本服是否為跨服 */
	private boolean fight;
	/** 对外跨服端口 */
	private int crossPort;

	/** redis server url {http:port} */
	private String redisUrl;

	private ServerConfig() {
	}

	public static ServerConfig getInstance() {
		return instance;
	}

	public void init() {
		String configFile = "server.xml";
		Element rootElement = XmlUtils.loadConfigRootElement(configFile);
		NodeList nodes = rootElement.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if ("game-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j = 0; j < subNodes.getLength(); j++) {
					if ("server_id".equals(subNodes.item(j).getNodeName())) {
						serverId = Integer.parseInt(subNodes.item(j).getTextContent());
					} else if ("server_port".equals(subNodes.item(j).getNodeName())) {
						serverPort = Integer.parseInt(subNodes.item(j).getTextContent());
					}
				}
			} else if ("http-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j = 0; j < subNodes.getLength(); j++) {
					if ("http_port".equals(subNodes.item(j).getNodeName())) {
						GateServerConfig.httpPort = Integer.parseInt(subNodes.item(j).getTextContent());
					} else if ("white_ips".equals(subNodes.item(j).getNodeName())) {
						String[] ips = subNodes.item(j).getTextContent().split(";");
						GateServerConfig.whiteIpPattern = ips;
					}
				}
			} else if ("cross-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j = 0; j < subNodes.getLength(); j++) {
					if ("match_url".equals(subNodes.item(j).getNodeName())) {
						this.matchUrl = subNodes.item(j).getTextContent();
					} else if ("fight".equals(subNodes.item(j).getNodeName())) {
						this.fight = NumberUtil.booleanValue(subNodes.item(j).getTextContent());
					} else if ("cross_port".equals(subNodes.item(j).getNodeName())) {
						this.crossPort = NumberUtil.intValue(subNodes.item(j).getTextContent());
					}
				}
			} else if ("redis-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j = 0; j < subNodes.getLength(); j++) {
					if ("url".equals(subNodes.item(j).getNodeName())) {
						this.redisUrl = subNodes.item(j).getTextContent();
					}
				}

			}
		}

		try {
			this.inetAddr = IpAddrUtil.getInnetIp();
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		logger.info("本服serverId为{},后台端口为{}", this.serverId, GateServerConfig.httpPort);
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
		return GateServerConfig.httpPort;
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

}
