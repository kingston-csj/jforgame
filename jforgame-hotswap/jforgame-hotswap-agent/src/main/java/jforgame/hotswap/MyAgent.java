package jforgame.hotswap;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MyAgent {
    // 目标类全限定名
    private static final String TARGET_CLASS = "jforgame.hotswap.JavaDoctor";

    // 缓存JavaDoctor的类加载器（JVM生命周期内唯一，无需重复获取）
    private static volatile ClassLoader CACHED_HOST_CLASS_LOADER = null;

    public static void agentmain(String args, Instrumentation inst) {
        ClassLoader classLoader = null;
        try {
            // ========== 核心：多方式兜底获取宿主类加载器 ==========
            classLoader = getHostClassLoader(inst);
            if (classLoader == null) {
                throw new RuntimeException("cannot get host class loader");
            }
            // 1. 用宿主类加载器加载JavaDoctor
            Class<?> javaDoctorClass = classLoader.loadClass(TARGET_CLASS);

            // 2. 读取fixData字段
            Field fixDataField = javaDoctorClass.getDeclaredField("fixData");
            fixDataField.setAccessible(true);
            byte[] fixData = (byte[]) fixDataField.get(null);

            if (fixData == null || fixData.length == 0) {
                throw new RuntimeException("fixData为空，无热更类数据");
            }

            // 3. 解析fixData中的类字节码
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

            // 4. 执行类重定义
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
                    // 异常赋值到exception字段，不写入独立日志
                    Field exceptionField = javaDoctorClass.getDeclaredField("exception");
                    exceptionField.setAccessible(true);
                    exceptionField.set(null, e);
                    return;
                }
            }

            // 5. 赋值log字段
            sb.append("] finished");
            Field logField = javaDoctorClass.getDeclaredField("log");
            logField.setAccessible(true);
            logField.set(null, sb.toString());

            // 清空异常字段
            Field exceptionField = javaDoctorClass.getDeclaredField("exception");
            exceptionField.setAccessible(true);
            exceptionField.set(null, null);

        } catch (Exception e) {
            try {
                // 异常赋值到exception字段供宿主程序感知
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
        // 1. 优先用缓存（JVM内只获取一次）
        if (CACHED_HOST_CLASS_LOADER != null) {
            return CACHED_HOST_CLASS_LOADER;
        }

        ClassLoader classLoader = null;

        // 2. 方式1（最优）：从Instrumentation已加载的类中提取
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

        // 3. 方式3（最终兜底）：获取SystemClassLoader（即AppClassLoader）
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        // 缓存获取到的类加载器（后续无需重复遍历）
        if (classLoader != null) {
            CACHED_HOST_CLASS_LOADER = classLoader;
        }

        return classLoader;
    }
}