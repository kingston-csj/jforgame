package com.kingston.jforgame.server.email;

import org.junit.Test;

import com.kingston.jforgame.server.monitor.email.EMailManager;

public class EmailTest {
	
	@Test
	public void sendEmail() throws Exception {
		EMailManager.getInstance().sendEmailSync("测试", "测试内容");
	}

}
