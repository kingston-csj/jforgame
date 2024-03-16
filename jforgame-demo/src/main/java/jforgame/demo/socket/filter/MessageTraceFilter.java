package jforgame.demo.socket.filter;

import com.google.gson.Gson;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MessageTraceFilter implements MessageHandler  {

	private Logger logger = LoggerFactory.getLogger(MessageTraceFilter.class);

	private boolean debug = true;

	@Override
	public boolean messageReceived(IdSession session, Object message)
			throws Exception {
		if (debug && traceRequest(message)) {
			logger.error("<<<<<<<<<<[{}]{}={}",
					session,
					message.getClass().getSimpleName(), new Gson().toJson(message));
		}
		return true;
	}

	private boolean traceRequest(Object message) {
		Set<Class<?>> ignores = new HashSet<>();

		return ! ignores.contains(message.getClass());
	}

}
