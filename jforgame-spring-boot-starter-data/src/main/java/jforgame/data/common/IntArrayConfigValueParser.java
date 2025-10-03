package jforgame.data.common;


import jforgame.commons.util.NumberUtil;
import jforgame.commons.util.SplitUtil;

/**
 * 将字符串转换为int数组，分隔符为逗号
 * 例如："1,2,3,4,5" 转化为 [1,2,3,4,5]
 */
public class IntArrayConfigValueParser implements ConfigValueParser<int[]> {

    @Override
    public int[] convert(String source) {
        String[] splits = source.split(SplitUtil.COMMA);
        int[] result = new int[splits.length];
        for (int i = 0; i < splits.length; i++) {
            result[i] = NumberUtil.intValue(splits[i]);
        }
        return result;
    }
}
