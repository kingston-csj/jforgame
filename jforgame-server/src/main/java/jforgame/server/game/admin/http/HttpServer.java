package jforgame.server.game.admin.http;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import jforgame.server.ServerConfig;
import jforgame.socket.ServerNode;
import jforgame.socket.session.SessionManager;

/**
 * @author kinson
 */
public class HttpServer implements ServerNode {

	private Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private IoAcceptor acceptor;

	private int port;

	public void start() throws Exception {
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new HttpServerCodec());
		acceptor.setHandler(new HttpServerHandle());

		ServerConfig serverConfig = ServerConfig.getInstance();
		this.port = serverConfig.getHttpPort();
		acceptor.bind(new InetSocketAddress(port));
	}

	public void shutdown() {
		if (acceptor != null) {
			acceptor.unbind();
			acceptor.dispose();
		}
		logger.error("---------> http server stop at port:{}", port);
	}
}

class HttpServerHandle extends IoHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		String ipAddr = SessionManager.INSTANCE.getRemoteIp(session);
		if (!isInWhiteIps(ipAddr)) {
			logger.error("非法后台登录,remoteIp=[{}]", ipAddr);
			byte[] body = "too young too simple".getBytes("UTF-8");
			IoBuffer out = IoBuffer.allocate(body.length);
			out.put(body);
			out.flip();
			session.write(out);
			session.close(false);
		}
	}

	private static boolean isInWhiteIps(String ip) {
		for (String pattern : ServerConfig.getInstance().getWhiteIpPattern()) {
			if (ip.matches(pattern)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
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
			// 响应的status line和header部分
			session.write(response);
			// 响应body部分
			session.write(responseIoBuffer);
		}
	}

	private HttpCommandResponse handleCommand(HttpRequest request) {
		HttpCommandParams httpParams = toHttpParams(request);
		if (httpParams == null) {
			HttpCommandResponse failed = HttpCommandResponse.valueOfFailed();
			failed.setMessage("参数错误");
			return failed;
		}
		logger.info("收到http后台命令 {}", httpParams);
		HttpCommandResponse commandResponse = HttpCommandManager.getInstance().handleCommand(httpParams);
		if (commandResponse == null) {
			HttpCommandResponse failed = HttpCommandResponse.valueOfFailed();
			failed.setMessage("该后台命令不存在");
			return failed;
		}
		return commandResponse;
	}

	@SuppressWarnings("unchecked")
	private HttpCommandParams toHttpParams(HttpRequest httpReq) {
		String cmd = httpReq.getParameter("cmd");
		if (StringUtils.isEmpty(cmd)) {
			return null;
		}
		String paramJson = httpReq.getParameter("params");
		Map<String, String> params = new HashMap<>();
		if (StringUtils.isNotEmpty(paramJson)) {
			try {
				params = new Gson().fromJson(paramJson, HashMap.class);
			} catch (Exception e) {
			}
		}
		return HttpCommandParams.valueOf(Integer.parseInt(cmd), params);
	}

}