package com.kingston.doctor;

import groovy.lang.GroovyClassLoader;

import com.kingston.utils.FileUtils;

public enum HotswapManager {

	INSTANCE;

	/**
	 * 
	 * @param classFullName
	 * @return
	 */
	public String loadClass(String classFullName) {
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
			return "加载class失败," + e.getMessage();
		}
		
		return "加载class成功";

	}

}
