package jforgame.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpAddrUtil {

	public static String getInnetIp() throws SocketException {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP
		Enumeration<NetworkInterface> netInterfaces;
		netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		boolean found = false;// 是否找到外网IP
		while (netInterfaces.hasMoreElements() && !found) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress()
						&& !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
					netip = ip.getHostAddress();
					found = true;
					break;
				} else if (ip.isSiteLocalAddress()
						&& !ip.isLoopbackAddress()&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}
		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getInnetIp());
	}

}
