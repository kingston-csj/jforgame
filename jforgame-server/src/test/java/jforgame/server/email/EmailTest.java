package jforgame.server.email;

import org.junit.Test;

import jforgame.server.monitor.email.EMailManager;

public class EmailTest {
	
	@Test
	public void sendEmail() throws Exception {
		EMailManager.getInstance().sendEmailSync("测试", "测试内容");
	}

}
