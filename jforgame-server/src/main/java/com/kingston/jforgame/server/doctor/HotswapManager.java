package com.kingston.jforgame.server.doctor;

import java.lang.management.ManagementFactory;

import com.kingston.jforgame.common.utils.FileUtils;
import com.sun.tools.attach.VirtualMachine;

import groovy.lang.GroovyClassLoader;

/**
 * 以javaAgent的方式热更文件
 * 只能修改java文件的方法体
 * @author kingston
 */
public enum HotswapManager {

	INSTANCE;

	/**
	 * load java source file and creates a new instance of the class
	 * @param classFullName
	 * @return
	 */
	public String loadJavaFile(String classFullName) {
		//类的名字，
		String simpleName = classFullName.substring(classFullName.lastIndexOf(".")+1, classFullName.length());
		try{
			String filePath = "script/" + simpleName +".java";
			String clazzFile = FileUtils.readText(filePath);
			@SuppressWarnings("resource")
			Class<?> clazz = new GroovyClassLoader().parseClass(clazzFile, classFullName);
			clazz.newInstance();
		}catch(Exception e) {
			e.printStackTrace();
			return "load class failed ," + e.getMessage();
		}

		return "load class succ";
	}

	/**
	 * use jdk instrument to hotswap a loaded class
	 * you can only modify a class's method!!
	 * @param className
	 * @return
	 */
	public boolean reloadClass(String className){
		try{
			//拿到当前jvm的进程id
			String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			VirtualMachine vm = VirtualMachine.attach(pid);
			String[] classStr = className.split("\\.");
			String path = "./hotswap/"+classStr[classStr.length-1]+".class";
			System.err.println("path=="+path);
			//path参数即agentmain()方法的第一个参数
			vm.loadAgent("./agent/hotswap-agent.jar",path);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}

		return true;
	}


}
