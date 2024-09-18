package jforgame.runtime.clazz;

import jforgame.runtime.util.StringUtil;

import java.lang.annotation.Annotation;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ClassStats {

    /**
     * 获取类的相关信息
     *
     * @param clazz jva类
     */
    public static Map<String, Object> showClassInfo(Class<?> clazz) {
        Map<String, Object> result = new HashMap<>();
        result.put("class-info", StringUtil.className(clazz));
        result.put("code-source", getCodeSource(clazz));
        result.put("isInterface", clazz.isInterface());
        result.put("isAnnotation", clazz.isAnnotation());
        result.put("isAnonymousClass", clazz.isAnonymousClass());
        result.put("isArray", clazz.isArray());
        result.put("isLocalClass", clazz.isLocalClass());
        result.put("isMemberClass", clazz.isMemberClass());
        result.put("isPrimitive", clazz.isPrimitive());
        result.put("isSynthetic", clazz.isSynthetic());
        result.put("modifier", StringUtil.modifier(clazz.getModifiers(), ','));
        result.put("annotation", drawAnnotation(clazz));
        result.put("interfaces", drawInterface(clazz));
        result.put("super-class", drawSuperClass(clazz));
        result.put("class-loader", drawClassLoader(clazz));

        return result;
    }

    private static String getCodeSource(Class<?> clazz) {
        CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (null == cs || null == cs.getLocation() || null == cs.getLocation().getFile()) {
            return "";
        }

        return cs.getLocation().getFile();
    }

    private static String drawAnnotation(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Annotation[] annotationArray = clazz.getDeclaredAnnotations();
        if (annotationArray.length > 0) {
            for (Annotation annotation : annotationArray) {
                sb.append(StringUtil.className(annotation.annotationType())).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb.toString();
    }

    private static String drawInterface(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Class<?>[] interfaceArray = clazz.getInterfaces();
        if (interfaceArray.length == 0) {
            sb.append(" ");
        } else {
            for (Class<?> i : interfaceArray) {
                sb.append(i.getName()).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    private static String drawSuperClass(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Class<?> curr = clazz;
        while (curr != Objects.class && curr != null) {
            sb.append(StringUtil.className(curr)).append(",");
            curr = curr.getSuperclass();
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }


    private static String drawClassLoader(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        ClassLoader loader = clazz.getClassLoader();
        if (null != loader) {
            sb.append(loader).append(",");
            while (true) {
                loader = loader.getParent();
                if (null == loader) {
                    break;
                }
                sb.append(loader).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
