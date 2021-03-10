package jforgame.server.cross.core.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jforgame.common.utils.ClassScanner;
import jforgame.server.cross.core.CrossCmdExecutor;
import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.annotation.RequestMapping;
import jforgame.socket.message.CmdExecutor;
import jforgame.socket.message.Message;

public class BaseCrossMessageDispatcher implements CMessageDispatcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static volatile BaseCrossMessageDispatcher self;

	/** [message.class, CmdExecutor] */
	private static final Map<Class<?>, CmdExecutor> HANDLERS = new HashMap<>();

	public static BaseCrossMessageDispatcher getInstance() {
		if (self != null) {
			return self;
		}
		synchronized (BaseCrossMessageDispatcher.class) {
			if (self == null) {
				BaseCrossMessageDispatcher instance = new BaseCrossMessageDispatcher();
				instance.initialize();
				self = instance;
			}
		}
		return self;
	}

	private void initialize() {
		Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation("com.kinson.jforgame.server",
				CrossController.class);

		for (Class<?> controller : controllers) {
			try {
				Object handler = controller.newInstance();
				Method[] methods = controller.getDeclaredMethods();
				for (Method method : methods) {
					RequestMapping mapperAnnotation = method.getAnnotation(RequestMapping.class);
					if (mapperAnnotation != null) {
						short[] meta = getMessageMeta(method);
						if (meta == null) {
							throw new RuntimeException(
									String.format("controller[%s] method[%s] lack of RequestMapping annotation",
											controller.getName(), method.getName()));
						}

						// 方法必須有兩個参数，第一个为SCSession或CCSession,第二个为Message子类
						Class<?>[] paramTypes = method.getParameterTypes();
						if (paramTypes.length != 2) {
							throw new RuntimeException(
									String.format("controller[%d] method[%d] must have two arguments",
											controller.getClass().getSimpleName(), method.getName()));
						}
						if (!(paramTypes[0] == SCSession.class || paramTypes[0] == CCSession.class)
								|| paramTypes[1].isAssignableFrom(Message.class)) {
							throw new RuntimeException(String.format("controller[%d] method[%d] arguments error",
									controller.getClass().getSimpleName(), method.getName()));
						}
						CmdExecutor cmdExecutor = HANDLERS.get(paramTypes[1]);
						if (cmdExecutor != null) {
							throw new RuntimeException(String.format("controller[%d] method[%d] duplicated",
									controller.getClass().getSimpleName(), method.getName()));
						}

						cmdExecutor = CmdExecutor.valueOf(method, method.getParameterTypes(), handler);
						HANDLERS.put(paramTypes[1], cmdExecutor);
					}
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	/**
	 * 返回方法所带Message参数的元信息
	 * 
	 * @param method
	 * @return
	 */
	private short[] getMessageMeta(Method method) {
		for (Class<?> paramClazz : method.getParameterTypes()) {
			if (Message.class.isAssignableFrom(paramClazz)) {
				MessageMeta protocol = paramClazz.getAnnotation(MessageMeta.class);
				if (protocol != null) {
					short[] meta = { protocol.module(), protocol.cmd() };
					return meta;
				}
			}
		}
		return null;
	}

	@Override
	public void serverDispatch(SCSession session, Message message) {
		CmdExecutor cmdHandler = HANDLERS.get(message.getClass());
		if (cmdHandler == null) {
			logger.error("{}找不到处理器", message.getClass().getSimpleName());
			return;
		}
		Object[] params = new Object[2];
		params[0] = session;
		params[1] = message;
		
		CrossCmdExecutor.getInstance().addTask(session, () -> {
			try {
				cmdHandler.getMethod().invoke(cmdHandler.getHandler(), params);
			} catch (Exception e) {
				logger.error("", e);
			}
		});
	}

	@Override
	public void clientDispatch(CCSession session, Message message) {
		CmdExecutor cmdHandler = HANDLERS.get(message.getClass());
		if (cmdHandler == null) {
			logger.error("{}找不到处理器", message.getClass().getSimpleName());
			return;
		}
		Object[] params = new Object[2];
		params[0] = session;
		params[1] = message;
		
		CrossCmdExecutor.getInstance().addTask(session, () -> {
			try {
				cmdHandler.getMethod().invoke(cmdHandler.getHandler(), params);
			} catch (Exception e) {
				logger.error("", e);
			}
		});
	}

}
