package jforgame.hotswap;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MyAgent {
    // Fully qualified name of the target class
    private static final String TARGET_CLASS = "jforgame.hotswap.JavaDoctor";

    // Cache the classloader of JavaDoctor (unique within the JVM lifecycle, no need to fetch it repeatedly)
    private static volatile ClassLoader CACHED_HOST_CLASS_LOADER = null;

    public static void agentmain(String args, Instrumentation inst) {
        ClassLoader classLoader = null;
        try {
            // ========== Core: obtain the host classloader through multiple fallback strategies ==========
            classLoader = getHostClassLoader(inst);
            if (classLoader == null) {
                throw new RuntimeException("cannot get host class loader");
            }
            // 1. Load JavaDoctor with the host classloader
            Class<?> javaDoctorClass = classLoader.loadClass(TARGET_CLASS);

            // 2. Read the fixData field
            Field fixDataField = javaDoctorClass.getDeclaredField("fixData");
            fixDataField.setAccessible(true);
            byte[] fixData = (byte[]) fixDataField.get(null);

            if (fixData == null || fixData.length == 0) {
                throw new RuntimeException("fixData is empty, no hot-swap class data");
            }

            // 3. Parse the class bytecode from fixData
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fixData));
            int fileSize = dis.readInt();

            Map<String, byte[]> reloadFiles = new HashMap<>();
            for (int i = 0; i < fileSize; i++) {
                String className = dis.readUTF();
                int bodySize = dis.readInt();
                byte[] body = new byte[bodySize];
                dis.read(body);
                reloadFiles.put(className, body);
            }

            // 4. Perform the class redefinition
            StringBuilder sb = new StringBuilder("redefine [");
            for (Map.Entry<String, byte[]> entry : reloadFiles.entrySet()) {
                String className = entry.getKey();
                try {
                    Class<?> targetClass = classLoader.loadClass(className);
                    ClassDefinition clazzDef = new ClassDefinition(targetClass, entry.getValue());
                    inst.redefineClasses(clazzDef);
                    sb.append(className).append(";");
                } catch (ClassNotFoundException ignore) {
                } catch (Exception e) {
                    // Assign the exception to the exception field; do not write to a separate log
                    Field exceptionField = javaDoctorClass.getDeclaredField("exception");
                    exceptionField.setAccessible(true);
                    exceptionField.set(null, e);
                    return;
                }
            }

            // 5. Assign the log field
            sb.append("] finished");
            Field logField = javaDoctorClass.getDeclaredField("log");
            logField.setAccessible(true);
            logField.set(null, sb.toString());

            // Clear the exception field
            Field exceptionField = javaDoctorClass.getDeclaredField("exception");
            exceptionField.setAccessible(true);
            exceptionField.set(null, null);

        } catch (Exception e) {
            try {
                // Assign the exception to the exception field so the host program can be aware of it
                Class<?> javaDoctorClass = classLoader.loadClass(TARGET_CLASS);
                Field exceptionField = javaDoctorClass.getDeclaredField("exception");
                exceptionField.setAccessible(true);
                exceptionField.set(null, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private static ClassLoader getHostClassLoader(Instrumentation inst) {
        // 1. Prefer the cache (fetched only once within the JVM)
        if (CACHED_HOST_CLASS_LOADER != null) {
            return CACHED_HOST_CLASS_LOADER;
        }

        ClassLoader classLoader = null;

        // 2. Strategy 1 (preferred): extract it from the classes already loaded by Instrumentation
        try {
            for (Class<?> clazz : inst.getAllLoadedClasses()) {
                if (TARGET_CLASS.equals(clazz.getName())) {
                    classLoader = clazz.getClassLoader();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Strategy 2 (final fallback): get the SystemClassLoader (i.e. AppClassLoader)
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        // Cache the obtained classloader (no need to iterate again later)
        if (classLoader != null) {
            CACHED_HOST_CLASS_LOADER = classLoader;
        }

        return classLoader;
    }
}