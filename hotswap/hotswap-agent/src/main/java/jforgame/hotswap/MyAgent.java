package jforgame.hotswap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * hot swap agent
 */
public class MyAgent {

    public static void agentmain(String args, Instrumentation inst) {
        Class<?> c = null;
        try {
            c = Class.forName("jforgame.hotswap.JavaDoctor");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DataInputStream dis = null;
        try {
            Field field = c.getDeclaredField("fixData");
            field.setAccessible(true);
            byte[] fixData = (byte[]) field.get(null);
            dis = new DataInputStream(new ByteArrayInputStream(fixData));

            Map<String, byte[]> reloadFiles = new HashMap<>();
            int fileSize = dis.readInt();
            for (int i = 0; i < fileSize; i++) {
                String fileName = dis.readUTF();
                int bodySize = dis.readInt();
                byte[] body = new byte[bodySize];
                dis.read(body);
                reloadFiles.put(fileName, body);
            }

            StringBuilder sb = new StringBuilder("redefine [" );

            ClassLoader scl = ClassLoader.getSystemClassLoader();
            for (Map.Entry<String, byte[]> entry : reloadFiles.entrySet()) {
                String fileName = entry.getKey();
                try {
                    if (scl.loadClass(fileName) != null) {
                        ClassDefinition clazzDef = new ClassDefinition(Class.forName(fileName, true,Thread.currentThread().getContextClassLoader()), entry.getValue());
                        inst.redefineClasses(new ClassDefinition[]{clazzDef});
                        sb.append( fileName + ";");
                    }
                } catch (ClassNotFoundException ignore) {

                }
            }

            sb.append( "] finished");
            Field field2 = c.getDeclaredField("log");
            field2.setAccessible(true);
            field2.set(null, sb.toString());
        } catch (Exception e) {
            try {
                Field field = c.getDeclaredField("exception");
                field.setAccessible(true);
                field.set(null, e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}
