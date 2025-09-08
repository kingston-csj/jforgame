package jforgame.commons;
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
     * @param word
     * @return
     */
    public static String firstLetterToUpperCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * 将单词的第一个字母小写
     *
     * @param word
     * @return
     */
    public static String firstLetterToLowerCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}
