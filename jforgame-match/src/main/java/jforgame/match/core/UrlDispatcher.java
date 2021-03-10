package jforgame.match.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.common.utils.ClassScanner;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;
import jforgame.socket.message.Message;


public class UrlDispatcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private volatile static UrlDispatcher instance;

	/** [message signature, CmdExecutor] */
	private static final Map<String, CmdExecutor> service2Handler = new HashMap<>();

	private static final Map<String, Class<?>> signature2Message = new HashMap<>();

	public static UrlDispatcher getInstance() {
		//double check
		if (instance == null) {
			synchronized (UrlDispatcher.class) {
				if (instance == null) {
					instance = new UrlDispatcher();
				}
			}
		}
		return instance;
	}

	private UrlDispatcher() {
		initialize();
	}

	public void initialize() {
		Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation("com.kinson.jforgame.match", Controller.class);

		for (Class<?> controller: controllers) {
			try {
				Object handler = controller.newInstance();
				Method[] methods = controller.getDeclaredMethods();
				for (Method method:methods) {
					RequestMapping mapperAnnotation = method.getAnnotation(RequestMapping.class);
					if (mapperAnnotation != null) {
						registerMethodInvoker(method, handler);
					}
				}
			}catch(Exception e) {
				logger.error("", e);
			}
		}
	}

	private void registerMethodInvoker(Method method, Object handler) {
		Class<?>[] params = method.getParameterTypes();
		for (Class<?> param:params) {
			if (Message.class.isAssignableFrom(param)) {
				String signature = buildSignature(param);
				CmdExecutor cmdExecutor = service2Handler.get(signature);
				if (cmdExecutor != null) {
					throw new RuntimeException(String.format("method signature duplicated", signature));
				}

				cmdExecutor = CmdExecutor.valueOf(method, method.getParameterTypes(), handler);
				service2Handler.put(signature, cmdExecutor);
				signature2Message.put(signature, param);
			}
		}
	}


	/**
	 * message entrance, in which io thread dispatch messages
	 * @param session
	 * @param message
	 */
	public void dispatch(IoSession session, Message message) {
		String signature = buildSignature(message.getClass());
		CmdExecutor cmdExecutor = service2Handler.get(signature);
		if (cmdExecutor == null) {
			logger.error("message executor missed, signature={}", signature);
			return;
		}

		Object[] params = convertToMethodParams(session, cmdExecutor.getParams(), message);
		Object controller = cmdExecutor.getHandler();
		try {
			//通过反射
			cmdExecutor.getMethod().invoke(controller, params);
		}catch(Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 将各种参数转为被RequestMapper注解的方法的实参
	 * @param session
	 * @param methodParams
	 * @param message
	 * @return
	 */
	private Object[] convertToMethodParams(IoSession session, Class<?>[] methodParams, Message message) {
		Object[] result = new Object[methodParams==null?0:methodParams.length];

		for (int i=0;i<result.length;i++) {
			Class<?> param = methodParams[i];
			if (IoSession.class.isAssignableFrom(param)) {
				result[i] = session;
			} else if (Message.class.isAssignableFrom(param)) {
				result[i] = message;
			}
		}

		return result;
	}

	private String buildSignature(Class<?> clazz) {
		return clazz.getSimpleName();
	}


	public Class<?> getMessageClazzBy(String signature) {
		return signature2Message.get(signature);
	}

}

