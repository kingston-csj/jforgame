package com.kingston.jforgame.server.monitor.jmx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * jmx客户端
 */
public class JmxClient {

	/**
	 * 该工具类简单无敌，试想一下，在生产环境，直接用一个ide就可以链接服务器的jmx接口，
	 * 从而可以查看服务器的内存数据，或者执行任意代码，想想都激动不已 ^_^
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		// 如果执行的JavaScript脚本内容过长
		// 则可以把脚本写在一个文件里，然后使用jmx client 动态调用mbean接口方法
        String user = "root";
        String pwd  = "root";

        // 如果生产环境需要账号验证的话
        String[] account = new String[] { user, pwd };
        Map<String, String[]> props = new HashMap<String, String[]>();
        props.put("jmx.remote.credentials", account);

        // 10086参数，具体见启动脚本的vm参数，@see -Dcom.sun.management.jmxremote.port=10086
        JMXServiceURL address =
        		new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:10086/jmxrmi");
        JMXConnector connector = JMXConnectorFactory.connect(address, props);
        MBeanServerConnection mBeanConnection = connector.getMBeanServerConnection();

        connector.connect();

        ObjectName objectName=new ObjectName("GameMXBean:name=GameMonitor");
        System.out.println("\nMBean count = " + mBeanConnection.getMBeanCount());

//        for (ObjectInstance mxBean : mBeanConnection.queryMBeans(null, null)) {
//            System.out.println("object.getObjectName="+mxBean.getObjectName());
//        }

        final GameMonitorMXBean mBean = JMX.newMBeanProxy(mBeanConnection, objectName,
        		GameMonitorMXBean.class);

        String script = readScript("script.js");
        System.err.println(script);

        System.err.println(mBean.execJavascript(script));
	}

	/**
	 * 读取js文件代码块源码
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private static String readScript(String fileName) throws Exception {
		File file = new File(fileName);
		String line;
		StringBuffer result = new StringBuffer("");

		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line=reader.readLine()) != null) {
				result.append(line).append("\n");
			}
		}

		return result.toString();
	}

}
