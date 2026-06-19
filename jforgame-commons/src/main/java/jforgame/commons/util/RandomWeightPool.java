package jforgame.commons.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Random weight pool
 * @since 2.4.0
 * @author Carson
 */
public abstract class RandomWeightPool<E> {

    /**
     * Weight list
     */
    private final List<Integer> weights = new ArrayList<>();

    /**
     * Random object list
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
     * Specifies the weight
     *
     * @param element the element
     * @return the weight
     */
    public abstract int getWeight(E element);


    /**
     * Randomly selects one result
     *
     * @return the random result
     */
    public E randomOne() {
        int index = RandomUtil.randomIndex(weights);
        return randomList.get(index);
    }

    /**
     * Randomly selects a list of results
     *
     * @param count  the number of results
     * @param remove whether to remove the randomly selected element from the list
     * @return the random results
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
