package com.kingston.game.http;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpResponse;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpVersion;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.google.gson.Gson;
import com.kingston.ServerConfig;

public class HttpServer {  

	public void start() throws Exception {  
		IoAcceptor acceptor = new NioSocketAcceptor();  
		acceptor.getFilterChain().addLast("codec", new HttpServerCodec());  
		acceptor.setHandler(new HttpServerHandle()); 
		int port = ServerConfig.getInstance().getHttpPort();
		acceptor.bind(new InetSocketAddress(port));  
	}  
}  

class HttpServerHandle extends IoHandlerAdapter {  

	@Override  
	public void exceptionCaught(IoSession session, Throwable cause)  
			throws Exception {  
		cause.printStackTrace();  
	}  

	@Override  
	public void messageReceived(IoSession session, Object message)  
			throws Exception {  
		if (message instanceof HttpRequest) {  
			// 请求，解码器将请求转换成HttpRequest对象  
			HttpRequest request = (HttpRequest) message;  
			HttpCommandResponse commandResponse = handleCommand(request);
			// 响应HTML  
			String responseHtml = new Gson().toJson(commandResponse);  
			byte[] responseBytes = responseHtml.getBytes("UTF-8");  
			int contentLength = responseBytes.length;  

			// 构造HttpResponse对象，HttpResponse只包含响应的status line和header部分  
			Map<String, String> headers = new HashMap<String, String>();  
			headers.put("Content-Type", "text/html; charset=utf-8");  
			headers.put("Content-Length", Integer.toString(contentLength));  
			HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK, headers);  

			// 响应BODY  
			IoBuffer responseIoBuffer = IoBuffer.allocate(contentLength);  
			responseIoBuffer.put(responseBytes);  
			responseIoBuffer.flip();  

			session.write(response); // 响应的status line和header部分  
			session.write(responseIoBuffer); // 响应body部分  
		}  
	}  

	private HttpCommandResponse handleCommand(HttpRequest request) {
		HttpCommandParams httpParams = toHttpParams(request);
		if (httpParams == null) {
			HttpCommandResponse failed = HttpCommandResponse.valueOfSucc();
			failed.setMessage("参数错误");
			return failed;
		}
		HttpCommandResponse commandResponse = HttpCommandManager.getInstance().handleCommand(httpParams);
		if (commandResponse == null) {
			HttpCommandResponse failed = HttpCommandResponse.valueOfSucc();
			failed.setMessage("该后台命令不存在");
			return failed;
		}
		return commandResponse;
	}

	private HttpCommandParams toHttpParams(HttpRequest httpReq) {
		String cmd = httpReq.getParameter("cmd"); 
		if (StringUtils.isEmpty(cmd)) {
			return null;
		}
		String paramJson = httpReq.getParameter("params"); 
		if (StringUtils.isNotEmpty(paramJson)) {
			try{
				Map<String, String> params = new Gson().fromJson(paramJson, HashMap.class);
				return HttpCommandParams.valueOf(Integer.parseInt(cmd), params);
			}catch(Exception e) {
			}
		}
		return null;
	}

}  