package jforgame.commons.utils;

import jforgame.commons.ds.CircleQueue;
import org.junit.Test;

public class TestCircleQueue {

    @Test
    public void testCapacity() {
        CircleQueue<Integer> queue = new CircleQueue<>(10);
        for (int i = 0; i < 99; i++) {
            queue.add(i);
        }
        Object[] queueArray = queue.getQueue();
        System.out.println("环形队列：");
        for (Object o : queueArray) {
            System.out.println(o);
        }
        System.out.println("capacity: " + queue.getCapacity());
        System.out.println("size: " + queue.size());
    }
}
