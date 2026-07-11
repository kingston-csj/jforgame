package jforgame.hotswap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 纯JDK原生API解析class文件，无ASM等外部依赖
 */
class ClassFileMeta {

    private byte[] data;
    private String className;

    public ClassFileMeta(File file) throws Exception {
        Path path = file.toPath();
        this.data = Files.readAllBytes(path);
        this.className = ClassByteUtil.parseClassNameFromRawClassBytes(this.data);
    }

    public byte[] getData() {
        return data;
    }

    public String getClassName() {
        return className;
    }

}