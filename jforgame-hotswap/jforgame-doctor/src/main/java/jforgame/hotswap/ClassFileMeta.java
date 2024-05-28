package jforgame.hotswap;


import org.objectweb.asm.ClassReader;

import java.io.File;
import java.nio.file.Files;

/**
 * This class provides a way to get bytes data and its className of a file.
 * It should be noted that the class uses some apis form tools.jar.
 */
class ClassFileMeta {

    byte[] data;

    String className;

    public ClassFileMeta(File file) throws Exception {
        data = Files.readAllBytes(file.toPath());
        className = readClassName(data);
    }

    private String readClassName(byte[] data)  {
        ClassReader classReader = new ClassReader(data);
        return classReader.getClassName().replaceAll("/",".");
    }

    public byte[] getData() {
        return data;
    }

    public String getClassName() {
        return className;
    }
}
