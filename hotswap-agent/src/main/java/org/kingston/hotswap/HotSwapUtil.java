package org.kingston.hotswap;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachine;

public class HotSwapUtil {

	private final static Logger logger = LoggerFactory.getLogger(HotSwapUtil.class);
	
	/**
	 * 执行结果是否成功
	 */
	static volatile ExecStatistics execResult;
	
	/**
	 * 执行结果是否成功
	 */
	static volatile boolean execSucc;

	public static synchronized String reloadClass(String path) {
		try {
			// 拿到当前jvm的进程id
			String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			VirtualMachine vm = VirtualMachine.attach(pid);
			String targetPath = "./hotswap/" + path;
			System.err.println("传入的路径为" + targetPath);
			// path参数即agentmain()方法的第一个参数

			vm.loadAgent("./agent/hotswap-agent.jar", targetPath);
//			System.err.println("热更结果为" + execResult.toString());
			return execResult.toString();
		} catch (Exception e) {
			logger.error("", e);
		}

		return "执行失败";
	}

}
