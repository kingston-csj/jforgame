package jforgame.runtime.util;

import java.lang.reflect.Modifier;

public class StringUtil {

    public static String beautifyName(String name) {
        return name.replace(' ', '_').toLowerCase();
    }

    public static boolean isEmpty(String content) {
        return content == null || content.trim().isEmpty();
    }

    /**
     * 翻译类名称
     *
     * @param clazz Java类
     * @return 翻译值
     */
    public static String className(Class<?> clazz) {
        if (clazz.isArray()) {
            StringBuilder sb = new StringBuilder(clazz.getName());
            sb.delete(0, 2);
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("[]");
            return sb.toString();
        } else {
            return clazz.getName();
        }
    }

    /**
     * 翻译Modifier值
     *
     * @param mod modifier
     * @return 翻译值
     */
    public static String modifier(int mod, char splitter) {
        StringBuilder sb = new StringBuilder();
        if (Modifier.isAbstract(mod)) {
            sb.append("abstract").append(splitter);
        }
        if (Modifier.isFinal(mod)) {
            sb.append("final").append(splitter);
        }
        if (Modifier.isInterface(mod)) {
            sb.append("interface").append(splitter);
        }
        if (Modifier.isNative(mod)) {
            sb.append("native").append(splitter);
        }
        if (Modifier.isPrivate(mod)) {
            sb.append("private").append(splitter);
        }
        if (Modifier.isProtected(mod)) {
            sb.append("protected").append(splitter);
        }
        if (Modifier.isPublic(mod)) {
            sb.append("public").append(splitter);
        }
        if (Modifier.isStatic(mod)) {
            sb.append("static").append(splitter);
        }
        if (Modifier.isStrict(mod)) {
            sb.append("strict").append(splitter);
        }
        if (Modifier.isSynchronized(mod)) {
            sb.append("synchronized").append(splitter);
        }
        if (Modifier.isTransient(mod)) {
            sb.append("transient").append(splitter);
        }
        if (Modifier.isVolatile(mod)) {
            sb.append("volatile").append(splitter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
