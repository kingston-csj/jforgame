package jforgame.common.utils;

import java.util.Random;

/**
 * Created by qizhao.liao 2019/9/29
 */
public class RandomUtil {

    private static ThreadLocal<Random> tr = new ThreadLocal<Random>(){
        @Override
        protected Random initialValue() {
            return new Random(System.currentTimeMillis());
        }
    };

    public static final Random getRandom() {
        return tr.get();
    }

    /**
     * 返回0（包括）至Integer.MAX_VALUE(不包括)之间随机的一个数
     * @return
     */
    public static final int nextInt() {
        return getRandom().nextInt();
    }

}
