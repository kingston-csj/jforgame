package com.kingston.net.dispatch;

import java.lang.reflect.Method;

public class CmdExecutor {

	private Method method;

	private Class<?>[] params;

	private Class<?> handler;

	public static CmdExecutor valueOf(Method method, Class<?>[] params, Class<?> handler) {
		CmdExecutor executor = new CmdExecutor();
		executor.method = method;
		executor.params = params;
		executor.handler = handler;

		return executor;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?>[] getParams() {
		return params;
	}

	public Class<?> getHandler() {
		return handler;
	}

}
