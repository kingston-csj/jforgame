package jforgame.hotswap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

    private final Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class.getName());

    private Map<String, byte[]> classByteDef;

    private DynamicClassLoader() {

    }

    public DynamicClassLoader(Map<String, byte[]> classByteDef) {
        super(DynamicClassLoader.class.getClassLoader());
        this.classByteDef = classByteDef;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = null;
        try {
            c = super.findClass(name);
        } catch (ClassNotFoundException ignore) {
        }

        if (c == null) {
            byte[] data = classByteDef.get(name);
            if (data == null) {
                throw new ClassNotFoundException(name);
            }
            c = super.defineClass(name, data, 0, data.length);
            logger.error("loaded new class {}", name);
        }
        return c;
    }

}
