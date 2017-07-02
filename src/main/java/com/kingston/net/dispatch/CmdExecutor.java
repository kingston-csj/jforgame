package com.kingston.net.dispatch;

import java.lang.reflect.Method;

/**
 * 消息执行单元封装
 * @author kingston
 *
 */
public class CmdExecutor {

	/** 业务处理的工作方法 */
	private Method method;
	/** 传递给工作方法的相关参数 */
	private Class<?>[] params;
	/** 控制器实例 */
	private Object handler;

	public static CmdExecutor valueOf(Method method, Class<?>[] params, Object handler) {
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

	public Object getHandler() {
		return handler;
	}

}
