package jforgame.demo.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Carson
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

    public static int randomValue(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    public static int randomList( final List<Integer> probabilityList ) {
        if (CollectionUtils.isEmpty(probabilityList)) {
            throw new IllegalArgumentException("probabilityList is empty");
        }
        for (Integer i : probabilityList) {
            if (i < 0) {
                throw new IllegalArgumentException("probabilityList contains negative number");
            }
        }
        return randomIndex(probabilityList);
    }

    public static int randomIndex(List<Integer> probs) {
        if (probs == null || probs.isEmpty()) {
            return -1;
        }
        int sum = probs.stream().reduce(0, Integer::sum);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 生成一个在 [0, sum) 范围内的随机数
        int randomValue = random.nextInt(sum);
        int accumulated = 0;
        // 遍历概率列表，通过累计概率来判断随机数落在哪个区间，从而确定索引
        for (int i = 0; i < probs.size(); i++) {
            accumulated += probs.get(i);
            if (randomValue < accumulated) {
                return i;
            }
        }
        // 理论不会跑到这里，语法需求
        throw new IllegalArgumentException("randomIndex out of range");
    }

    public static List<Integer> randomList(List<Integer> probabilityList, int count, boolean remove) {
        if (CollectionUtils.isEmpty(probabilityList)) {
            throw new IllegalArgumentException("probabilityList is empty");
        }
        for (Integer prob : probabilityList) {
            if (prob < 0) {
                throw new IllegalArgumentException("probabilityList contains negative number");
            }
        }
        if (count <= 0) {
            throw new IllegalArgumentException("count <= 0");
        }
        if (remove && count > probabilityList.size()) {
            throw new IllegalArgumentException("count > probabilityList size");
        }
        List<Integer> hits = new ArrayList<>(count);
        if (remove) {
            for (int i = 0; i < count; i++) {
                int index = randomIndex(probabilityList);
                hits.add(index);
                probabilityList.set(index, 0);
            }
        } else {
            for (int i = 0; i < count; i++) {
                int index = randomIndex(probabilityList);
                hits.add(index);
            }
        }
        return hits;
    }

//    public static <E> List<E> randomWeightList()

}
