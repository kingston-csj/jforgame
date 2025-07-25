package jforgame.commons;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 随机权重池
 * @since 2.4.0
 * @author Carson
 */
public abstract class RandomWeightPool<E> {

    /**
     * 权重列表
     */
    private final List<Integer> weights = new ArrayList<>();

    /**
     * 随机对象列表
     */
    private final List<E> randomList;

    public RandomWeightPool(Collection<E> randomList) {
        this.randomList = new ArrayList<>(randomList);
        Iterator<E> iterator = this.randomList.iterator();
        while (iterator.hasNext()) {
            E element = iterator.next();
            int weight = getWeight(element);
            if (weight <= 0) {
                iterator.remove();
            } else {
                weights.add(weight);
            }
        }
    }

    /**
     * 指定权重
     *
     * @param element 元素
     * @return 权重
     */
    public abstract int getWeight(E element);


    /**
     * 随机一个结果
     *
     * @return 随机结果
     */
    public E randomOne() {
        int index = RandomUtil.randomIndex(weights);
        return randomList.get(index);
    }

    /**
     * 随机一个结果列表
     *
     * @param count  数量
     * @param remove 被随机的元素是否从列表中移除
     * @return 随机结果
     */
    public List<E> randomList(int count, boolean remove) {
        if (remove && count > randomList.size()) {
            throw new IllegalArgumentException("count cannot larger than pool's size");
        }
        List<E> results = new ArrayList<>(count);
        List<Integer> indexs = RandomUtil.randomIndexList(weights, count, remove);
        indexs.forEach(i -> results.add(randomList.get(i)));
        return results;
    }


}
