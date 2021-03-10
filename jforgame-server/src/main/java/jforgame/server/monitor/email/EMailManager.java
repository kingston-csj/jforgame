package jforgame.server.monitor.email;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.utils.XmlUtils;

public class EMailManager {

	private static volatile EMailManager instance;

	private ExecutorService service;

	private EMailConfig emailConfig;

	public static EMailManager getInstance() {
		if (instance == null) {
			EMailManager self = new EMailManager();
			self.service = Executors.newSingleThreadExecutor(new NamedThreadFactory("monitor-mail"));
			self.emailConfig = XmlUtils.loadXmlConfig("mail.xml", EMailConfig.class);
			self.emailConfig.init();
			instance = self;
		}
		return instance;
	}
	
	/**
	 * 异步发送邮件
	 * @param title
	 * @param content
	 */
	public void sendEmailAsyn(String title, String content) {
		service.execute(() -> {
			sendEmailSync(title, content);
		});
	}

	/**
	 * 同步发送邮件
	 * @param title
	 * @param content
	 */
	public void sendEmailSync(String title, String content) {
		try { // 1. 创建参数配置, 用于连接邮件服务器的参数配置
			Properties props = new Properties(); // 参数配置
			props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
			props.setProperty("mail.smtp.host", emailConfig.getSenderEmailSMTPHost()); // 发件人的邮箱的 SMTP 服务器地址

			// 2. 根据配置创建会话对象, 用于和邮件服务器交互
			Session session = Session.getInstance(props);
			// 设置为debug模式, 可以查看详细的发送 log
			session.setDebug(true);

			// 4. 根据 Session 获取邮件传输对象
			Transport transport = session.getTransport();
			transport.connect(emailConfig.getSenderEmailAddr(), emailConfig.getSenderEmailPwd());

			for (String receiver : emailConfig.getReceivers()) {
				// 3. 创建一封邮件
				MimeMessage message = createMimeMessage(session, emailConfig.getSenderEmailAddr(), receiver, title, content);
				// 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人,
				// 密送人
				transport.sendMessage(message, message.getAllRecipients());
			}

			// 7. 关闭连接
			transport.close();
		} catch (Exception e) {
			LoggerUtils.error("", e);
		}
	}

	/**
	 * 创建一封只包含文本的简单邮件
	 *
	 * @param session     和服务器交互的会话
	 * @param sendMail    发件人邮箱
	 * @param receiveMail 收件人邮箱
	 * @return
	 * @throws Exception
	 */
	private MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String title, String content) throws Exception {
		// 1. 创建一封邮件
		MimeMessage message = new MimeMessage(session);

		// 2. From: 发件人
		message.setFrom(new InternetAddress(sendMail, "昵称", "UTF-8"));

		// 3. To: 收件人（可以增加多个收件人、抄送、密送）
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "XX用户", "UTF-8"));

		// 4. Subject: 邮件主题
		message.setSubject(title, "UTF-8");

		// 5. Content: 邮件正文（可以使用html标签）
		message.setContent(content, "text/html;charset=UTF-8");
		// 6. 设置发件时间
		message.setSentDate(new Date());

		// 7. 保存设置
		message.saveChanges();

		return message;
	}

}
