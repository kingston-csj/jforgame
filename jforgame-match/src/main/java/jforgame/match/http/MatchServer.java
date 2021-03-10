package jforgame.match.http;

import java.net.InetSocketAddress;
import java.net.URLDecoder;

import jforgame.match.core.UrlDispatcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import jforgame.socket.message.Message;

/**
 * 匹配服http服务
 * @author kinson
 */
public class MatchServer {

	private Logger logger = LoggerFactory.getLogger(MatchServer.class);

	private IoAcceptor acceptor;

	/** http端口 */
	int port;

	public void start(int port) throws Exception {
		this.port = port;
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new HttpServerCodec());
		acceptor.setHandler(new HttpServerHandle());

		acceptor.bind(new InetSocketAddress(port));

		logger.error("---------> http server start at port:{}", port);
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

	private static Logger logger = LoggerFactory.getLogger(MatchServer.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
	}

	@Override
	public void messageReceived(IoSession session, Object urlParams)
			throws Exception {
		if (urlParams instanceof HttpRequest) {
			// 请求，解码器将请求转换成HttpRequest对象
			HttpRequest request = (HttpRequest) urlParams;
			Message msg = parseHttpRequest(request);
			UrlDispatcher.getInstance().dispatch(session, msg);
		}
	}

	@SuppressWarnings("unchecked")
	private Message parseHttpRequest(HttpRequest httpReq) {
		String service = httpReq.getParameter("service");
		if (StringUtils.isEmpty(service)) {
			return null;
		}

		Class<?> clazz = UrlDispatcher.getInstance().getMessageClazzBy(service);
		String paramJson = httpReq.getParameter("param");
		if (StringUtils.isNotEmpty(paramJson)) {
			try{
				return (Message)new Gson().fromJson(URLDecoder.decode(paramJson), clazz);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

