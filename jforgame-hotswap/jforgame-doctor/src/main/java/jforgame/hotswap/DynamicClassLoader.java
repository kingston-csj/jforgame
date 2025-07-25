package jforgame.hotswap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class loader provides a way to load new class in runtime.
 * If {@link #loadClass(String)} is invoked, this loader will follow the delegation model for loading classes.
 * If {@link #findClass(String)} is invoked directly, this loader will NOT
 * follow the delegation model for loading classes and then creates a new class.
 */
public class DynamicClassLoader extends ClassLoader {

    private final Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class.getName());

    private final Map<String, byte[]> classBytes;

    /**
     * loading class data and its className
     *
     * @param directoryPath the directory path you want to scan
     */
    public DynamicClassLoader(String directoryPath) {
        this.classBytes = FileUtil.readClassData(directoryPath);
    }

    public DynamicClassLoader(Map<String, byte[]> classBytes) {
        this.classBytes = classBytes;
    }

    /**
     * This method should follow the delegation model for loading classes
     *
     * @param name – The binary name of the class
     * @return The resulting Class object
     */
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = null;
        try {
            c = super.findClass(name);
        } catch (ClassNotFoundException ignore) {
        }

        if (c == null) {
            byte[] data = classBytes.get(name);
            if (data == null) {
                throw new ClassNotFoundException(name);
            }
            try {
                // 这里指定app classloader，不能用上下文加载器,因为如果在springmvc接口触发热更时,这里的classloader是tomcat的TomcatEmbeddedWebappClassLoader
                // 转由app classloader加载,应用程序才可以直接使用, 否则会报 ClassNotFoundException
                // 不能用DynamicClassLoader本身
                ClassLoader loader = ClassLoader.getSystemClassLoader();
                Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                method.setAccessible(true);
                method.invoke(loader, name, data, 0, data.length);
                logger.error("loaded new class {}", name);
            } catch (Exception e) {
                logger.error("load class {} failed", name, e);
            }
        }
        return c;
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }
}
