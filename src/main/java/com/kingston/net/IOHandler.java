package com.kingston.net;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class IOHandler extends IoHandlerAdapter {
	
	@Override 
	public void sessionCreated(IoSession session) { 
		//显示客户端的ip和端口 
		System.out.println(session.getRemoteAddress().toString()); 
	} 
	
	@Override 
	public void messageReceived(IoSession session, Object data ) throws Exception 
	{ 
		Message message = (Message)data;
		System.out.println("收到消息-->" + message); 
	} 
} 