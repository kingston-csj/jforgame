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
     * The app classloader is specified here; do not use the context classloader, because when hot-swapping is
     * triggered from a springmvc endpoint, the classloader here is tomcat's TomcatEmbeddedWebappClassLoader.
     * For new classes, delegating to the app classloader allows the application to use them directly,
     * otherwise a ClassNotFoundException will be thrown.
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
        // Check whether there is bytecode for a class we need to load dynamically
        byte[] data = classBytes.get(name);
        if (data == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            // Check whether the application classloader has already loaded this class
            boolean isLoadedByAppClassLoader = isLoadedByAppClassLoader(name);
            if (isLoadedByAppClassLoader) {
                // If already loaded by appClassLoader, reload it with the current classloader
                logger.info("reload class {} with dynamicClassLoader", name);
                return defineClass(name, data, 0, data.length);
            } else {
                // If not loaded yet, try to load it with appClassLoader
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
            // Try to load the class with appClassLoader; if it succeeds, the class has already been loaded
            appClassLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Class<?> defineClassWithAppClassLoader(String name, byte[] data) throws Exception {
        // Check whether appClassLoader has already defined this class
        try {
            return appClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            // If not, use reflection to let appClassLoader define this class
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
