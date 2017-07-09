package com.kingston;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.kingston.utils.XmlUtils;

public class ServerConfig {
	
	private Logger logger = LoggerFactory.getLogger(ServerConfig.class.getSimpleName());

	private static ServerConfig instance = new ServerConfig();
	/** 服务器id */
	private int serverId;
	/** 服务器端口 */
	private int serverPort;

	private ServerConfig() {}

	public static ServerConfig getInstance() {
		return instance;
	}

	public void initFromConfigFile() {
		String configFile = "server.xml";
		Element rootElement = XmlUtils.loadConfigRootElement(configFile);
		rootElement.getFirstChild().getNodeValue();
		String serverIdValue = rootElement.getElementsByTagName("server_id").item(0).getTextContent();
		this.serverId = Integer.parseInt(serverIdValue);
		String serverPortValue = rootElement.getElementsByTagName("server_port").item(0).getTextContent();
		this.serverPort = Integer.parseInt(serverPortValue);
		
		logger.info("本服serverId为{}", serverId);
	}

	public int getServerId() {
		return serverId;
	}
	
	public int getServerPort() {
		return serverPort;
	}

}
