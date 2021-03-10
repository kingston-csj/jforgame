package jforgame.common.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类扫描器
 * 
 * @author kinson
 */
public class ClassScanner {

	private static Logger logger = LoggerFactory.getLogger(ClassScanner.class);

	/**
	 * 默认过滤器（无实现）
	 */
	private final static Predicate<Class<?>> EMPTY_FILTER = clazz -> true;

	/**
	 * 扫描目录下的所有class文件
	 * 
	 * @param scanPackage 搜索的包根路径
	 * @return
	 */
	public static Set<Class<?>> getClasses(String scanPackage) {
		return getClasses(scanPackage, EMPTY_FILTER);
	}

	/**
	 * 返回所有的子类（不包括抽象类）
	 * 
	 * @param scanPackage 搜索的包根路径
	 * @param parent
	 * @return
	 */
	public static Set<Class<?>> listAllSubclasses(String scanPackage, Class<?> parent) {
		return getClasses(scanPackage, (clazz) -> {
			return parent.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers());
		});
	}

	/**
	 * 返回所有带制定注解的class列表
	 * 
	 * @param scanPackage 搜索的包根路径
	 * @param annotation
	 * @return
	 */
	public static <A extends Annotation> Set<Class<?>> listClassesWithAnnotation(String scanPackage,
			Class<A> annotation) {
		return getClasses(scanPackage, (clazz) -> {
			return clazz.getAnnotation(annotation) != null;
		});
	}

	/**
	 * 扫描目录下的所有class文件
	 * 
	 * @param pack   包路径
	 * @param filter 自定义类过滤器
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack, Predicate<Class<?>> filter) {
		Set<Class<?>> result = new LinkedHashSet<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, result, filter);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					Set<Class<?>> jarClasses = findClassFromJar(url, packageName, packageDirName, recursive, filter);
					result.addAll(jarClasses);
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}

		return result;
	}

	private static Set<Class<?>> findClassFromJar(URL url, String packageName, String packageDirName, boolean recursive,
			Predicate<Class<?>> filter) {
		Set<Class<?>> result = new LinkedHashSet<Class<?>>();
		try {
			// 获取jar
			JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
			// 从此jar包 得到一个枚举类
			Enumeration<JarEntry> entries = jar.entries();
			// 同样的进行循环迭代
			while (entries.hasMoreElements()) {
				// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				// 如果是以/开头的
				if (name.charAt(0) == '/') {
					// 获取后面的字符串
					name = name.substring(1);
				}
				// 如果前半部分和定义的包名相同
				if (name.startsWith(packageDirName)) {
					int idx = name.lastIndexOf('/');
					// 如果以"/"结尾 是一个包
					if (idx != -1) {
						// 获取包名 把"/"替换成"."
						packageName = name.substring(0, idx).replace('/', '.');
					}
					// 如果可以迭代下去 并且是一个包
					if ((idx != -1) || recursive) {
						// 如果是一个.class文件 而且不是目录
						if (name.endsWith(".class") && !entry.isDirectory()) {
							// 去掉后面的".class" 获取真正的类名
							String className = name.substring(packageName.length() + 1, name.length() - 6);
							try {
								// 添加到classes
								Class<?> c = Class.forName(packageName + '.' + className);
								if (filter.test(c)) {
									result.add(c);
								}
							} catch (ClassNotFoundException e) {
								logger.error("", e);  
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		}
		return result;
	}

	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, Set<Class<?>> classes, Predicate<Class<?>> filter) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			@Override
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes, filter);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					Class<?> clazz = Thread.currentThread().getContextClassLoader()
							.loadClass(packageName + '.' + className);
					if (filter.test(clazz)) {
						classes.add(clazz);
					}
				} catch (ClassNotFoundException e) {
					logger.error("", e);  
				}
			}
		}
	}

}