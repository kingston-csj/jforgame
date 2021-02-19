package com.kingston.jforgame.server.net;

import com.kingston.jforgame.common.utils.ClassScanner;
import com.kingston.jforgame.server.game.GameContext;
import com.kingston.jforgame.server.game.database.user.player.PlayerEnt;
import com.kingston.jforgame.server.thread.ThreadCenter;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.actor.CmdMail;
import com.kingston.jforgame.socket.actor.MailBox;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.annotation.RequestMapping;
import com.kingston.jforgame.socket.message.CmdExecutor;
import com.kingston.jforgame.socket.message.IMessageDispatcher;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageDispatcher implements IMessageDispatcher {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/** [module_cmd, CmdExecutor] */
	private static final Map<String, CmdExecutor> MODULE_CMD_HANDLERS = new HashMap<>();

	public MessageDispatcher() {
		initialize();
	}

	private void initialize() {
		Set<Class<?>> controllers = ClassScanner.listClassesWithAnnotation("com.kingston.jforgame.server.game",
				Controller.class);

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
						short module = meta[0];
						short cmd = meta[1];
						String key = buildKey(module, cmd);
						CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(key);
						if (cmdExecutor != null) {
							throw new RuntimeException(String.format("module[%d] cmd[%d] duplicated", module, cmd));
						}

						cmdExecutor = CmdExecutor.valueOf(method, method.getParameterTypes(), handler);
						MODULE_CMD_HANDLERS.put(key, cmdExecutor);
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
	public void onSessionCreated(IdSession session) {
		session.setAttribute(SessionProperties.DISTRIBUTE_KEY,
				SessionManager.INSTANCE.getNextSessionId());
	}

	@Override
	public void dispatch(IdSession session, Message message) {
		short module = message.getModule();
		short cmd = message.getCmd();

		CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(buildKey(module, cmd));
		if (cmdExecutor == null) {
			logger.error("message executor missed, module={},cmd={}", module, cmd);
			return;
		}

		Object[] params = convertToMethodParams(session, cmdExecutor.getParams(), message);
		Object controller = cmdExecutor.getHandler();

		CmdMail task = CmdMail.valueOf(session, controller, cmdExecutor.getMethod(), params);
		// 丢到任务消息队列，不在io线程进行业务处理
		selectMailQueue(session, message).onMessageReceive(task);
	}

	private MailBox selectMailQueue(IdSession session, Message message) {
		MailBox mailBox = message.mailQueue();
		if (mailBox != null) {
			return mailBox;
		}
		long playerId = session.getOwnerId();
		if (playerId > 0) {
			PlayerEnt player = GameContext.getPlayerManager().get(playerId);
			return player.mailQueue();
		}
		// TODO why here??
		return ThreadCenter.getLoginQueue().getSharedMailQueue(session.hashCode());
	}

	/**
	 * 将各种参数转为被RequestMapper注解的方法的实参
	 * 
	 * @param session
	 * @param methodParams
	 * @param message
	 * @return
	 */
	private Object[] convertToMethodParams(IdSession session, Class<?>[] methodParams, Message message) {
		Object[] result = new Object[methodParams == null ? 0 : methodParams.length];

		for (int i = 0; i < result.length; i++) {
			Class<?> param = methodParams[i];
			if (IdSession.class.isAssignableFrom(param)) {
				result[i] = session;
			} else if (Long.class.isAssignableFrom(param)) {
				result[i] = session.getOwnerId();
			} else if (long.class.isAssignableFrom(param)) {
				result[i] = session.getOwnerId();
			} else if (Message.class.isAssignableFrom(param)) {
				result[i] = message;
			}
		}

		return result;
	}

	private String buildKey(short module, short cmd) {
		return module + "_" + cmd;
	}

	@Override
	public void onSessionClosed(IdSession session) {
		long playerId = SessionManager.INSTANCE.getPlayerIdBy(session);
		if (playerId > 0) {
			logger.info("角色[{}]close session", playerId);
			PlayerEnt player = GameContext.getPlayerManager().get(playerId);
			player.mailQueue().onMessageReceive(() -> {
				GameContext.getPlayerManager().playerLogout(playerId);
			});
		}
	}

}