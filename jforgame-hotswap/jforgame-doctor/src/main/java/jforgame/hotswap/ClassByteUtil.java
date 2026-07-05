package jforgame.hotswap;

class ClassByteUtil {

    /**
     * 解析class文件，读取字节码，获取内部类名
     */
    public static String parseClassNameFromRawClassBytes(byte[] bytes) {
        int offset = 0;
        int magic = readU4(bytes, offset);
        offset += 4;
        if (magic != 0xCAFEBABE) {
            throw new IllegalArgumentException("Not a valid class file, magic error");
        }
        // 跳过版本号 minor + major
        offset += 4;

        int constantPoolCount = readU2(bytes, offset);
        offset += 2;

        // 两个缓存：
        // 1. utf8Pool：下标→字符串（tag=1）
        String[] utf8Pool = new String[constantPoolCount];
        // 2. classRefPool：CONSTANT_Class下标 → 对应utf8下标（tag=7）
        int[] classRefPool = new int[constantPoolCount];

        for (int i = 1; i < constantPoolCount; ) {
            int tag = bytes[offset] & 0xFF;
            offset += 1;
            switch (tag) {
                case 1: // CONSTANT_Utf8
                    int len = readU2(bytes, offset);
                    offset += 2;
                    byte[] strBuf = new byte[len];
                    System.arraycopy(bytes, offset, strBuf, 0, len);
                    offset += len;
                    utf8Pool[i] = new String(strBuf);
                    i++;
                    break;
                case 3: // Integer
                case 4: // Float
                    offset += 4;
                    i++;
                    break;
                case 5: // Long
                case 6: // Double
                    offset += 8;
                    i += 2;
                    break;
                case 7: // CONSTANT_Class：关键修复点
                    int nameIdx = readU2(bytes, offset);
                    offset += 2;
                    classRefPool[i] = nameIdx; // 保存它指向的utf8下标
                    i++;
                    break;
                case 8: // String
                case 16: // MethodType
                    offset += 2;
                    i++;
                    break;
                case 9: // Fieldref
                case 10: // Methodref
                case 11: // InterfaceMethodref
                case 12: // NameAndType
                case 18: // InvokeDynamic
                    offset += 4;
                    i++;
                    break;
                case 15: // MethodHandle
                    offset += 3;
                    i++;
                    break;
                default:
                    throw new IllegalArgumentException("unknown constant pool tag:" + tag);
            }
        }

        // 跳过 access_flags
        offset += 2;
        // this_class 是 CONSTANT_Class 的下标
        int classInfoIndex = readU2(bytes, offset);
        offset += 2;

        // 1. 通过class下标拿到utf8下标
        int utf8Index = classRefPool[classInfoIndex];
        // 2. 取出真正的内部类名
        String internalName = utf8Pool[utf8Index];
        if (internalName == null) {
            throw new IllegalArgumentException("Failed resolve class name, invalid class file");
        }
        return internalName.replace('/', '.');
    }

    private static int readU2(byte[] b, int off) {
        return ((b[off] & 0xFF) << 8) | (b[off + 1] & 0xFF);
    }

    private static int readU4(byte[] b, int off) {
        return (b[off] << 24)
                | ((b[off + 1] & 0xFF) << 16)
                | ((b[off + 2] & 0xFF) << 8)
                | (b[off + 3] & 0xFF);
    }
}
