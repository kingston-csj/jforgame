package jforgame.commons.ds;

import jforgame.commons.thread.NotThreadSafe;

import java.util.Arrays;

/**
 * 循环队列，数据结构的环形。永远保存最新的N条记录。
 * @param <T> 泛型参数
 */
@NotThreadSafe
public class CircleQueue<T>
{
    private final int capacity;
    private final Object[] elementData;
    private int head = 0;

    private int tail = 0;

    public CircleQueue()
    {
        this.capacity = 100;
        this.elementData = new Object[this.capacity];
    }

    public CircleQueue(int initSize)
    {
        this.capacity = initSize;
        this.elementData = new Object[this.capacity];
    }

    public int size()
    {
        if (isEmpty())
            return 0;
        if (isFull()) {
            return this.capacity;
        }
        return this.tail + 1;
    }

    public void add(T element)
    {
        if (isEmpty()) {
            this.elementData[0] = element;
        } else if (isFull()) {
            this.elementData[this.head] = element;
            this.head += 1;
            this.tail += 1;
            this.head = (this.head == this.capacity ? 0 : this.head);
            this.tail = (this.tail == this.capacity ? 0 : this.tail);
        } else {
            this.elementData[(this.tail + 1)] = element;
            this.tail += 1;
        }
    }

    public boolean isEmpty() {
        return (this.tail == this.head) && (this.tail == 0) && (this.elementData[this.tail] == null);
    }

    public boolean isFull() {
        return ((this.head != 0) && (this.head - this.tail == 1)) || ((this.head == 0) && (this.tail == this.capacity - 1));
    }

    public void clear() {
        Arrays.fill(this.elementData, null);
        this.head = 0;
        this.tail = 0;
    }

    public Object[] getQueue()
    {
        Object[] elementDataSort = new Object[this.capacity];
        Object[] elementDataCopy = this.elementData.clone();
        if (!isEmpty())
        {
            int indexMax;
            int indexSort;
            int i;
            if (isFull()) {
                indexMax = this.capacity;
                indexSort = 0;
                for (i = this.head; i < indexMax; ) {
                    elementDataSort[indexSort] = elementDataCopy[i];
                    indexSort++;
                    i++;
                    if (i == this.capacity) {
                        i = 0;
                        indexMax = this.head;
                    }
                }
            }
            else {
                if (this.tail >= 0) System.arraycopy(elementDataCopy, 0, elementDataSort, 0, this.tail);
            }
        }
        return elementDataSort;
    }

    public int getCapacity() {
        return capacity;
    }


}