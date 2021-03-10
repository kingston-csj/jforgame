package jforgame.server.doctor;

//import com.kinson.hotswap.HotSwapUtil;
import jforgame.common.utils.FileUtils;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * 以javaAgent的方式热更文件 只能修改java文件的方法体
 * 
 * @author kinson
 */
public enum HotswapManager {

	/** 枚举单例 */
	INSTANCE;
	
	/**
	 * 热更拓展参数
	 */
	private Map<String, Object> extendParams;
	
	
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
			String filePath = getFilePath("groovy" + File.separator + simpleName + ".java");
			String clazzFile = FileUtils.readLines(filePath);
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
//		return HotSwapUtil.reloadClass(getFilePath(path));
		return null;
	}
	
	private String getFilePath(String dir) {
		String defaultDir = System.getProperty("scriptDir");
		String fullPath = dir;
		if (StringUtils.isNoneBlank(defaultDir)) {
			fullPath = dir + File.separator + dir;
		}
		return fullPath;
	}
	
	public Object getParamValue(String key) {
		return extendParams.get(key);
	}
	
	public void updateParamValue(String key, Object value) {
		this.extendParams.put(key, value);
	}

}
