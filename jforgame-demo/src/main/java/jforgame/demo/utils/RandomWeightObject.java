package jforgame.demo.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Carson
 */
public abstract class RandomWeightObject<E> {

    private final List<Integer> weights = new ArrayList<>();

    private final List<E> randomList;

    public RandomWeightObject(Collection<E> randomList) {
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

    public List<E> getRandomList() {
        return randomList;
    }

    public abstract int getWeight(E element);


    public E randomOneResult() {
        int index = RandomUtil.randomList(weights);
        return randomList.get(index);
    }

    public List<E> randomOneResult(int count, boolean remove) {
        List<E> results = new ArrayList<>(count);
        List<Integer> indexs = RandomUtil.randomList(weights, count, remove);
        indexs.forEach(i -> results.add(randomList.get(i)));
        return results;
    }


}
