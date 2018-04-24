package org.kingston.hotswap;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class MyAgent {


	public static void agentmain(String args,Instrumentation inst) {
		//该方法支持在JVM 启动后再启动代理，对应清单的Agent-Class:属性
		try{
			System.err.println("传进来的参数为"+args);
			File f = new File(args);
			byte[] targetClassFile = new byte[(int)f.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(targetClassFile);
			dis.close();

			DynamicClassLoader myLoader = new DynamicClassLoader();
			Class targetClazz = myLoader.findClass(targetClassFile);
			System.err.println("目标class类全路径为"+targetClazz.getName());
			ClassDefinition clazzDef = new ClassDefinition(Class.forName(targetClazz.getName()), targetClassFile);
			inst.redefineClasses(clazzDef);

			System.err.println("重新定义"+args+"完成！！");

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
