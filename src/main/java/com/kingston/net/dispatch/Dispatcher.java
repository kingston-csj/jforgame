package com.kingston.net.dispatch;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.net.Message;
import com.kingston.net.annotation.MessageHandler;
import com.kingston.net.annotation.MessageInvoke;
import com.kingston.net.annotation.Protocol;
import com.kingston.utils.ClassFilter;
import com.kingston.utils.ClassScanner;

public enum Dispatcher {

	INSTANCE;
	
	private final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	
	private static final Map<String, CmdExecutor> MODULE_CMD_HANDLERS = new HashMap<>();
	/** key=class类型,value=handler实例 */
	private static final Map<Class<?>, Object> CLAZZ_HANDLERS = new HashMap<>();
	
	private Dispatcher() {
		
	}
	
	
	private void initalize() {
		Set<Class<?>> handlerClazzs = ClassScanner.getClasses("", new ClassFilter() {
			@Override
			public boolean accept(Class<?> clazz) {
				return clazz.getAnnotation(MessageHandler.class) != null;
			}
		});
		
		for (Class<?> clazz: handlerClazzs) {
			Method[] methods = clazz.getDeclaredMethods();
			short module = 0;
			short cmd = 0;
			for (Method method:methods) {
				MessageInvoke invokeAnnotation = method.getAnnotation(MessageInvoke.class);
				if (invokeAnnotation != null) {
					Class<?> messageClazz = null;
					for (Class<?> paramClazz: method.getParameterTypes()) {
						if (Message.class.isAssignableFrom(paramClazz)) {
							Protocol protocol = paramClazz.getAnnotation(Protocol.class);
							if (protocol != null) {
								module = protocol.module();
								cmd = protocol.cmd();
								break;
							}
						}
					}
				}
			}
			
			String key = buildKey(module, cmd);
			CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(key);
			if (cmdExecutor != null) {
				throw new RuntimeException(String.format("module[%d] cmd[%d]重复", module, cmd));
			}
			
			MODULE_CMD_HANDLERS.put(key, cmdExecutor);
			try{
				Object handler = clazz.newInstance();
				CLAZZ_HANDLERS.put(clazz, handler);
			}catch(Exception e) {
				logger.error("", e);
			}
			
		}
	}
	
	
	public void dispatch(IoSession session, Message message) {
		short module = message.getModule();
		short cmd    = message.getCmd();
		
		CmdExecutor cmdExecutor = MODULE_CMD_HANDLERS.get(buildKey(module, cmd));
		if (cmdExecutor == null) {
			logger.error("请求协议不存在,module=[%d],cmd=[%d]", module, cmd);
			return;
		}
	}
	
	private String buildKey(short module, short cmd) {
		return module + "_" + cmd;
	}
	
	
}
