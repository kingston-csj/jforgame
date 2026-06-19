package jforgame.commons.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Random utility class
 * Created by Carson
 *
 * @since 2.4.0
 */
public class RandomUtil {

    /**
     * Returns a random number between 0 (inclusive) and Integer.MAX_VALUE (exclusive)
     *
     * @return a random number
     */
    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    /**
     * Returns a random number between 0 (inclusive) and n (exclusive)
     *
     * @param n the upper bound (exclusive) of the random number
     * @return a random number
     */
    public static int nextInt(int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }

    /**
     * Returns a random number between min (inclusive) and max (exclusive)
     *
     * @param min the lower bound (inclusive) of the random number
     * @param max the upper bound (exclusive) of the random number
     * @return a random number
     */
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
     * Randomly selects an index based on weights
     *
     * @param probs the list of weights
     * @return the selected index
     */
    public static int randomIndex(List<Integer> probs) {
        if (probs == null || probs.isEmpty()) {
            return -1;
        }
        int sum = probs.stream().reduce(0, Integer::sum);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // Generate a random number in the range [0, sum)
        int randomValue = random.nextInt(sum);
        int accumulated = 0;
        // Iterate through the probability list, determine which interval the random number falls in by accumulating probabilities, thus determining the index
        for (int i = 0; i < probs.size(); i++) {
            accumulated += probs.get(i);
            if (randomValue < accumulated) {
                return i;
            }
        }
        // Theoretically should not reach here, syntax requirement
        throw new IllegalArgumentException("randomIndex out of range");
    }

    /**
     * Randomly selects a list of indices based on weights
     *
     * @param probabilityList the list of weights
     * @param count           the number of random selections
     * @param remove          whether to remove the selected element from the list
     * @return the list of selected indices
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
     * Randomly selects one object
     *
     * @param objects  the list of objects
     * @param function the weight function for each object
     * @param <E>      the object type
     * @return the randomly selected object
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
