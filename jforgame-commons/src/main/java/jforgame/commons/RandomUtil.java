package jforgame.commons;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * 随机工具类
 * Created by Carson
 * @since 2.4.0
 */
public class RandomUtil {

    /**
     * 返回0（包括）至Integer.MAX_VALUE(不包括)之间随机的一个数
     *
     * @return
     */
    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int nextInt(int n) {
        return ThreadLocalRandom.current().nextInt(n);
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


    /**
     * 根据权重随机一个索引
     *
     * @param probs 权重列表
     * @return 索引
     */
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

    /**
     * 根据权重随机一个索引列表
     *
     * @param probabilityList 权重列表
     * @param count           随机数量
     * @param remove          被挑中后是否移除
     * @return 索引列表
     */
    public static List<Integer> randomIndexList(List<Integer> probabilityList, int count, boolean remove) {
        if (probabilityList == null || probabilityList.isEmpty()) {
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

    /**
     * 随机一个对象
     *
     * @param objects  对象列表
     * @param function 对象权重函数
     * @param <E>      对象类型
     * @return 随机对象
     */
    public static <E> E randomOne(Collection<E> objects, Function<E, Integer> function) {
        int totalWeight = 0;
        for (E object : objects) {
            totalWeight += function.apply(object);
        }
        if (totalWeight <= 0) {
            return null;
        }
        int random = nextInt(totalWeight);
        int weight = 0;
        for (E object : objects) {
            weight += function.apply(object);
            if (weight >= random) {
                return object;
            }
        }
        return null;

    }

}
