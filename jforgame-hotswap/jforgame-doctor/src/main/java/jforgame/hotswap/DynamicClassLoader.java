package jforgame.hotswap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class loader provides a way to load new class in runtime.
 * If {@link #loadClass(String)} is invoked, this loader will follow the delegation model for loading classes.
 * If {@link #findClass(String)} is invoked directly, this loader will NOT
 * follow the delegation model for loading classes and then creates a new class if not loaded by app classloader.
 */
public class DynamicClassLoader extends ClassLoader {

    private final Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class.getName());

    private final Map<String, byte[]> classBytes;

    /**
     * 这里指定app classloader，不能用上下文加载器,因为如果在springmvc接口触发热更时,这里的classloader是tomcat的TomcatEmbeddedWebappClassLoader
     * 对于新类，转由app classloader加载,应用程序才可以直接使用, 否则会报 ClassNotFoundException
     */
    private final ClassLoader appClassLoader;

    /**
     * loading class data and its className
     *
     * @param directoryPath the directory path you want to scan
     */
    public DynamicClassLoader(String directoryPath) {
        this.classBytes = FileUtil.readClassData(directoryPath);
        this.appClassLoader = ClassLoader.getSystemClassLoader();
    }

    public DynamicClassLoader(Map<String, byte[]> classBytes) {
        this.classBytes = classBytes;
        this.appClassLoader = ClassLoader.getSystemClassLoader();
    }

    /**
     * This method should follow the delegation model for loading classes
     *
     * @param name – The binary name of the class
     * @return The resulting Class object
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // 检查是否有我们需要动态加载的类字节码
        byte[] data = classBytes.get(name);
        if (data == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            // 检查应用类加载器是否已经加载了这个类
            boolean isLoadedByAppClassLoader = isLoadedByAppClassLoader(name);
            if (isLoadedByAppClassLoader) {
                // 如果已被appClassLoader加载，用当前类加载器重新加载
                logger.info("reload class {} with dynamicClassLoader", name);
                return defineClass(name, data, 0, data.length);
            } else {
                // 如果未被加载，尝试用appClassLoader加载
                logger.info("load new class {} with AppClassLoader", name);
                return defineClassWithAppClassLoader(name, data);
            }
        } catch (Exception e) {
            logger.error("Failed to load class {}", name, e);
            throw new ClassNotFoundException("Failed to load class " + name, e);
        }
    }

    private boolean isLoadedByAppClassLoader(String className) {
        try {
            // 尝试用appClassLoader加载类，如果成功则说明已加载
            appClassLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Class<?> defineClassWithAppClassLoader(String name, byte[] data) throws Exception {
        // 检查appClassLoader是否已经定义了这个类
        try {
            return appClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            // 如果没有，使用反射让appClassLoader定义这个类
            Method method = ClassLoader.class.getDeclaredMethod(
                    "defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            return (Class<?>) method.invoke(appClassLoader, name, data, 0, data.length);
        }
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }
}
