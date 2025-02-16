package jforgame.commons;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分隔符工具
 * 包括常见的分隔符，以及常见的解析方式
 */
public final class SplitUtil {

    public static final String UNDERLINE = "_";

    public static final String COMMA = ",|，";   //逗号

    public static final String AND = "&";

    public static final String EQUAL = "=";  //等号

    public static final String SEMICOLON = ";|；";  //分号

    public static final String SLASH = "/";  //斜杆

    /**
     * 解析成格式 {1001:1, 1002:2}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  单元分隔符
     * @param valueDelimiter 内容分隔符
     * @return key, value均为int类型的map
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
     * 解析成格式 {"1001":1, "1002":2}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  单元分隔符
     * @param valueDelimiter 内容分隔符
     * @return key, value均为int类型的map
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
     * 解析成格式 {"1001":"1", "1002":"2"}
     *
     * @param params         1001=1;1002=2
     * @param unitDelimiter  单元分隔符
     * @param valueDelimiter 内容分隔符
     * @return key, value均为int类型的map
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
