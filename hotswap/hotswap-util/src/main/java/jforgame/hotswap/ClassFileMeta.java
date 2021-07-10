package jforgame.hotswap;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPoolException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class ClassFileMeta {

    byte[] data;

    String md5;

    String className;

    ClassFileMeta(File file) throws Exception {
        data = Files.readAllBytes(file.toPath());
        className = readClassName(data);
    }

    private String readClassName(byte[] data) throws IOException, ConstantPoolException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        return ClassFile.read(dis).getName().replaceAll("/", ".");
    }

}
