package jforgame.commons.util;

/**
 * 字符串工具类
 */
public final class StringUtil {

    private StringUtil() {
    }

    public static boolean isEmpty(String word) {
        return word == null || word.isEmpty();
    }

    public static boolean isNotEmpty(String word) {
        return !isEmpty(word);
    }

    /**
     * 将单词的第一个字母大写
     *
     * @param word 输入单词
     * @return 首字母大写后的单词
     */
    public static String firstLetterToUpperCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 将单词的第一个字母小写
     *
     * @param word 输入单词
     * @return 首字母小写后的单词
     */
    public static String firstLetterToLowerCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}
