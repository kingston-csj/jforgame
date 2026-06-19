package jforgame.commons.util;

/**
 * String utility class
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
     * Capitalizes the first letter of a word
     *
     * @param word the input word
     * @return the word with the first letter capitalized
     */
    public static String firstLetterToUpperCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Lowercases the first letter of a word
     *
     * @param word the input word
     * @return the word with the first letter lowercased
     */
    public static String firstLetterToLowerCase(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}
