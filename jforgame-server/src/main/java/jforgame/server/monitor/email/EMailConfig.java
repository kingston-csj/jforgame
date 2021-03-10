package jforgame.server.monitor.email;

import java.util.HashSet;
import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 不同邮件服务的配置可能略有不同，这里以网易163邮箱为例
 * 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）
 */
@Root
public class EMailConfig {
	
	/**
	 * 邮件发送方地址
	 */
	@Element(required = true)
	private String senderEmailAddr;
	
	/**
	 * 邮件发送方密码
	 */
	@Element(required = true)
	private String senderEmailPwd;
	
	/**
	 * 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
	 */
	@Element(required = true)
	private String senderEmailSMTPHost;
	
	/**
	 * 接受方邮箱列表（多个用分号隔开）
	 */
	@Element(required = true)
	private String receiverEmailAdds;
	
	private Set<String> receivers = new HashSet<>();
	
	public void init() {
		String[] addrs = receiverEmailAdds.split(";");
		for (String addr : addrs) {
			receivers.add(addr);
		}
	}

	public String getSenderEmailAddr() {
		return senderEmailAddr;
	}

	public void setSenderEmailAddr(String senderEmailAddr) {
		this.senderEmailAddr = senderEmailAddr;
	}

	public String getSenderEmailPwd() {
		return senderEmailPwd;
	}

	public void setSenderEmailPwd(String senderEmailPwd) {
		this.senderEmailPwd = senderEmailPwd;
	}

	public String getSenderEmailSMTPHost() {
		return senderEmailSMTPHost;
	}

	public void setSenderEmailSMTPHost(String senderEmailSMTPHost) {
		this.senderEmailSMTPHost = senderEmailSMTPHost;
	}

	public String getReceiverEmailAdds() {
		return receiverEmailAdds;
	}

	public void setReceiverEmailAdds(String receiverEmailAdds) {
		this.receiverEmailAdds = receiverEmailAdds;
	}

	public Set<String> getReceivers() {
		return receivers;
	}

}
