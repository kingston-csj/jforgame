package jforgame.socket.netty;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import jforgame.socket.share.IdSession;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ChannelUtils {
	
public static AttributeKey<IdSession> SESSION_KEY = AttributeKey.valueOf("session");
	
	/**
	 * @param channel
	 * @param session
	 * @return true if this channel has bound its session
	 */
	public static boolean duplicateBindingSession(Channel channel, IdSession session) {
		Attribute<IdSession> sessionAttr = channel.attr(SESSION_KEY);
		return !sessionAttr.compareAndSet(null, session);
	}
	
	public static IdSession getSessionBy(Channel channel) {
		Attribute<IdSession> sessionAttr = channel.attr(SESSION_KEY);
		return sessionAttr.get() ;
	}
	
	public static String parseRemoteAddress(Channel channel) {
		if (null == channel) {
			return "";
		} else {
			SocketAddress remote = channel.remoteAddress();
			return doParse(remote != null ? remote.toString().trim() : "");
		}
	}

	public static String parseLocalAddress(Channel channel) {
		if (null == channel) {
			return "";
		} else {
			SocketAddress local = channel.localAddress();
			return doParse(local != null ? local.toString().trim() : "");
		}
	}

	public static String parseRemoteIP(Channel channel) {
		if (null == channel) {
			return "";
		} else {
			InetSocketAddress remote = (InetSocketAddress)channel.remoteAddress();
			return remote != null ? remote.getAddress().getHostAddress() : "";
		}
	}

	public static String parseRemoteHostName(Channel channel) {
		if (null == channel) {
			return "";
		} else {
			InetSocketAddress remote = (InetSocketAddress)channel.remoteAddress();
			return remote != null ? remote.getAddress().getHostName() : "";
		}
	}

	public static String parseLocalIP(Channel channel) {
		if (null == channel) {
			return "";
		} else {
			InetSocketAddress local = (InetSocketAddress)channel.localAddress();
			return local != null ? local.getAddress().getHostAddress() : "";
		}
	}

	public static int parseRemotePort(Channel channel) {
		if (null == channel) {
			return -1;
		} else {
			InetSocketAddress remote = (InetSocketAddress)channel.remoteAddress();
			return remote != null ? remote.getPort() : -1;
		}
	}

	public static int parseLocalPort(Channel channel) {
		if (null == channel) {
			return -1;
		} else {
			InetSocketAddress local = (InetSocketAddress)channel.localAddress();
			return local != null ? local.getPort() : -1;
		}
	}

	public static String parseSocketAddressToString(SocketAddress socketAddress) {
		return socketAddress != null ? doParse(socketAddress.toString().trim()) : "";
	}

	public static String parseSocketAddressToHostIp(SocketAddress socketAddress) {
		InetSocketAddress addrs = (InetSocketAddress)socketAddress;
		if (addrs != null) {
			InetAddress addr = addrs.getAddress();
			if (null != addr) {
				return addr.getHostAddress();
			}
		}

		return "";
	}

	private static String doParse(String addr) {
		if (addr == null || addr.isEmpty()) {
			return "";
		} else if (addr.charAt(0) == '/') {
			return addr.substring(1);
		} else {
			int len = addr.length();

			for(int i = 1; i < len; ++i) {
				if (addr.charAt(i) == '/') {
					return addr.substring(i + 1);
				}
			}

			return addr;
		}
	}
}
