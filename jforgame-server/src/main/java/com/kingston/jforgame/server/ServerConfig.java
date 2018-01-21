package com.kingston.jforgame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kingston.jforgame.net.socket.GateServerConfig;
import com.kingston.jforgame.server.utils.XmlUtils;

public class ServerConfig {

	private Logger logger = LoggerFactory.getLogger(ServerConfig.class.getSimpleName());

	private static ServerConfig instance = new ServerConfig();

	/** redis server url {http:port} */
	private String redisUrl;

	private ServerConfig() {}

	public static ServerConfig getInstance() {
		return instance;
	}

	public void initFromConfigFile() {
		String configFile = "server.xml";
		Element rootElement = XmlUtils.loadConfigRootElement(configFile);
		NodeList nodes = rootElement.getChildNodes();

		for (int i=0;i<nodes.getLength();i++) {
			Node node = nodes.item(i);
			if ("game-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j=0;j<subNodes.getLength();j++) {
					if ("server_id".equals(subNodes.item(j).getNodeName())) {
						GateServerConfig.serverId = Integer.parseInt(subNodes.item(j).getTextContent());
					} else if ("server_port".equals(subNodes.item(j).getNodeName())) {
						GateServerConfig.serverPort = Integer.parseInt(subNodes.item(j).getTextContent());
					}
				}
			} else if ("http-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j=0;j<subNodes.getLength();j++) {
					if ("http_port".equals(subNodes.item(j).getNodeName())) {
						GateServerConfig.httpPort = Integer.parseInt(subNodes.item(j).getTextContent());
					} else if ("white_ips".equals(subNodes.item(j).getNodeName())) {
						String[] ips = subNodes.item(j).getTextContent().split(";");
						GateServerConfig.whiteIpPattern = ips;
					}
				}
			} else if ("redis-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j=0;j<subNodes.getLength();j++) {
					if ("url".equals(subNodes.item(j).getNodeName())) {
						this.redisUrl = subNodes.item(j).getTextContent();
					}
				}

			}
		}

		logger.info("本服serverId为{},后台端口为{}", GateServerConfig.serverId, GateServerConfig.httpPort);
	}

	public int getServerId() {
		return GateServerConfig.serverId;
	}

	public int getServerPort() {
		return GateServerConfig.serverPort;
	}

	public int getHttpPort() {
		return GateServerConfig.httpPort;
	}

	public String getRedisUrl() {
		return redisUrl;
	}

}
