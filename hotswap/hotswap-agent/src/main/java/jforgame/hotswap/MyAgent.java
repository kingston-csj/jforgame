package jforgame.hotswap;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

/**
 * agent代理
 * 
 * @author kinson
 */
public class MyAgent {

	public static void agentmain(String args, Instrumentation inst)
	{
		Class<?> c = null;
		try
		{
			c = Class.forName("jforgame.hotswap.HotSwapUtil");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			StringBuilder sb = new StringBuilder();
			sb.append(args).append("\n");
			File f = new File(args);
			byte[] targetClassFile = new byte[(int)f.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(f));
			dis.readFully(targetClassFile);
			dis.close();

			DynamicClassLoader myLoader = new DynamicClassLoader();
			Class targetClazz = myLoader.findClass(targetClassFile);
//			sb.append("目标class全路径" + targetClazz.getName()).append("\n");

			ClassDefinition clazzDef = new ClassDefinition(Class.forName(targetClazz.getName()), targetClassFile);
			inst.redefineClasses(new ClassDefinition[] { clazzDef });

			sb.append("重新定义" + args + "完成");

			Field field = c.getField("log");
			field.set(null, sb.toString());
		}
		catch (Exception e)
		{
			Field field = null;
			try
			{
				field = c.getField("exception");
				field.set(null, e);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	private static void reloadClass(File f, Instrumentation inst) throws Exception {
		byte[] targetClassFile = new byte[(int) f.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		dis.readFully(targetClassFile);
		dis.close();

		DynamicClassLoader myLoader = new DynamicClassLoader();
		Class<?> targetClazz = myLoader.findClass(targetClassFile);
		System.err.println("目标class类全路径为" + targetClazz.getName());
		ClassDefinition clazzDef = new ClassDefinition(Class.forName(targetClazz.getName()), targetClassFile);
		inst.redefineClasses(clazzDef);

		System.err.println("重新定义" + targetClazz.getName() + "完成！！");
	}
}
