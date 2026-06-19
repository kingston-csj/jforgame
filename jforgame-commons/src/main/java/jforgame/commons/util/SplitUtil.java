package jforgame.commons.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Delimiter utility
 * Includes common delimiters and common parsing methods
 */
public final class SplitUtil {

    public static final String UNDERLINE = "_";

    public static final String COMMA = ",|，";   // Comma

    public static final String AND = "&";

    public static final String EQUAL = "=";  // Equal sign

    public static final String SEMICOLON = ";|；";  // Semicolon

    public static final String SLASH = "/";  // Slash

    /**
     * Parses into format {1001:1, 1002:2}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  unit delimiter
     * @param valueDelimiter content delimiter
     * @return a map with both key and value as int type
     */
    public static Map<Integer, Integer> toIntIntMap(String params, String unitDelimiter, String valueDelimiter) {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        String[] splits = params.split(unitDelimiter);

        for (String split : splits) {
            String[] unit = split.split(valueDelimiter);
            result.put(NumberUtil.intValue(unit[0]), NumberUtil.intValue(unit[1]));
        }

        return result;
    }

    /**
     * Parses into format {"1001":1, "1002":2}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  unit delimiter
     * @param valueDelimiter content delimiter
     * @return a map with key as String and value as int type
     */
    public static Map<String, Integer> toStringIntMap(String params, String unitDelimiter, String valueDelimiter) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String[] splits = params.split(unitDelimiter);

        for (String split : splits) {
            String[] unit = split.split(valueDelimiter);
            result.put(unit[0], NumberUtil.intValue(unit[1]));
        }

        return result;
    }

    /**
     * Parses into format {"1001":"1", "1002":"2"}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  unit delimiter
     * @param valueDelimiter content delimiter
     * @return a map with both key and value as String type
     */
    public static Map<String, String> toStringStringMap(String params, String unitDelimiter, String valueDelimiter) {
        Map<String, String> result = new LinkedHashMap<>();
        String[] splits = params.split(unitDelimiter);

        for (String split : splits) {
            String[] unit = split.split(valueDelimiter);
            result.put(unit[0], unit[1]);
        }

        return result;
    }

}
