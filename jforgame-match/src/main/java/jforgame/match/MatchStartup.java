package jforgame.match;

import jforgame.match.game.ladder.service.LadderCenterManager;
import jforgame.match.http.MatchServer;
import jforgame.match.util.XmlUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MatchStartup {

	public static void main(String[] args) throws Exception {
		int port = readPortFromXml();
		if (port <= 0) {
			throw new IllegalArgumentException("端口错误");
		}
		new MatchServer().start(port);
		
		LadderCenterManager.getInstance().init();
	}

	private static int readPortFromXml() {
		String configFile = "server.xml";
		Element rootElement = XmlUtils.loadConfigRootElement(configFile);
		NodeList nodes = rootElement.getChildNodes();

		for (int i=0;i<nodes.getLength();i++) {
			Node node = nodes.item(i);
			if ("http-server".equals(node.getNodeName())) {
				NodeList subNodes = node.getChildNodes();
				for (int j=0;j<subNodes.getLength();j++) {
					if ("http_port".equals(subNodes.item(j).getNodeName())) {
						return Integer.parseInt(subNodes.item(j).getTextContent());
					} else if ("white_ips".equals(subNodes.item(j).getNodeName())) {
					}
				}
			}
		}
		return 0;
	}

}
