package com.kingston.jforgame.server.doctor;

import org.kingston.hotswap.HotSwapUtil;

import com.kingston.jforgame.common.utils.FileUtils;

import groovy.lang.GroovyClassLoader;

/**
 * 以javaAgent的方式热更文件 只能修改java文件的方法体
 * 
 * @author kingston
 */
public enum HotswapManager {

	/** 枚举单例 */
	INSTANCE;

	/**
	 * load java source file and creates a new instance of the class
	 * 
	 * @param classFullName
	 * @return
	 */
	public String loadJavaFile(String classFullName) {
		// 类的名字，
		String simpleName = classFullName.substring(classFullName.lastIndexOf(".") + 1, classFullName.length());
		try {
			String filePath = "script/" + simpleName + ".java";
			String clazzFile = FileUtils.readText(filePath);
			@SuppressWarnings("resource")
			Class<?> clazz = new GroovyClassLoader().parseClass(clazzFile, classFullName);
			clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return "load class failed ," + e.getMessage();
		}

		return "load class succ";
	}

	/**
	 * use jdk instrument to hotswap a loaded class you can only modify a class's
	 * method!!
	 * 
	 * @param path 热更目录
	 * @return
	 */
	public String reloadClass(String path) {
		return HotSwapUtil.reloadClass(path);
	}

}
