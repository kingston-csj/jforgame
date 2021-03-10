package jforgame.orm.utils;

public final class StringUtils {

	private StringUtils() {}

	public static boolean isEmpty(String word) {
		return word == null || word.length() <= 0;
	}

	public static boolean isNotEmpty(String word) {
		return !isEmpty(word);
	}

	/**
	 * 将单词的第一个字母大写
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
	 * @param word
	 * @return
	 */
	public static String firstLetterToLowerCase(String word) {
		StringBuilder sb = new StringBuilder(word);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

}
