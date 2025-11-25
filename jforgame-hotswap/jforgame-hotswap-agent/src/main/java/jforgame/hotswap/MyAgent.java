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

    public static void agentmain(String args, Instrumentation inst) {
        ClassLoader hostClassLoader = null;
        try {
            // ========== 核心：多方式兜底获取宿主类加载器 ==========
            hostClassLoader = getHostClassLoader(inst);
            if (hostClassLoader == null) {
                throw new RuntimeException("cannot get host class loader");
            }
            // 1. 用宿主类加载器加载JavaDoctor
            Class<?> javaDoctorClass = hostClassLoader.loadClass(TARGET_CLASS);

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
                    Class<?> targetClass = hostClassLoader.loadClass(className);
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
                Class<?> javaDoctorClass = hostClassLoader.loadClass(TARGET_CLASS);
                Field exceptionField = javaDoctorClass.getDeclaredField("exception");
                exceptionField.setAccessible(true);
                exceptionField.set(null, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 多方式兜底获取宿主类加载器（保留核心修复逻辑）
     */
    private static ClassLoader getHostClassLoader(Instrumentation inst) {
        ClassLoader classLoader = null;

        // 方式1：从Instrumentation已加载的类中提取（最优）
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

        // 方式2：线程上下文类加载器（兜底）
        if (classLoader == null) {
            try {
                classLoader = Thread.currentThread().getContextClassLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 方式3：系统类加载器（最终兜底）
        if (classLoader == null) {
            try {
                classLoader = ClassLoader.getSystemClassLoader();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return classLoader;
    }
}