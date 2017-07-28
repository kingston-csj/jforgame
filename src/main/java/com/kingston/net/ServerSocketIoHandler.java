package com.kingston.net;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.kingston.logs.LoggerUtils;
import com.kingston.net.dispatch.MessageDispatcher;

public class ServerSocketIoHandler extends IoHandlerAdapter {
	
	@Override 
	public void sessionCreated(IoSession session) { 
		//显示客户端的ip和端口 
		System.out.println(session.getRemoteAddress().toString()); 
		session.setAttributeIfAbsent(SessionProperties.DISTRIBUTE_KEY,
				SessionManager.INSTANCE.getNextDistributeKey());
	} 
	
	@Override 
	public void messageReceived(IoSession session, Object data ) throws Exception 
	{ 
		Message message = (Message)data;
		System.err.println("收到消息-->" + message); 
		//交由消息分发器处理
		MessageDispatcher.getInstance().dispatch(session, message);
		
	} 
	
	 public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		 LoggerUtils.error("server exception", cause);
	 }
} 