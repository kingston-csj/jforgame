package jforgame.common.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import jforgame.common.thread.ThreadSafe;

/**
 * LinkedBlockingQueue的增强版（队伍内未消费的元素保证是不重复的）
 * @author kinson
 */
@ThreadSafe
public class BlockingUniqueQueue<E> extends LinkedBlockingQueue<E> {

	private static final long serialVersionUID = 8351632564634804122L;

	private Set<E> datas = new HashSet<>();

	@Override
	public boolean contains(Object o) {
		return datas.contains(o);
	}

	@Override
	 public boolean containsAll(Collection<?> c) {
		return datas.containsAll(c);
	}

	@Override
	public E take() {
		//该方法千万不要用 synchronized 修饰，不然一旦take()方法被阻塞了，另外一条线程就永远不能add了
		E head = null;
		try{
			head = super.take();
		}catch(Exception e){

		}
		datas.remove(head);
		return head;

	}

	@Override
	public synchronized boolean add(E e) {
		if (contains(e)) {
			return false;
		}
		datas.add(e);
		return super.add(e);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			if (add(e)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public synchronized boolean remove(Object o) {
		datas.remove(o);
		return super.remove(o);
	}

	@Override
	public synchronized boolean removeAll(Collection<?> c) {
		for (Object e:c) {
			datas.remove(e);
		}
		return super.removeAll(c);
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr(super.iterator());
	}

	private class Itr implements Iterator<E> {

		private Iterator<E> it;

		private E curr;

		Itr(Iterator<E> it) {
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return this.it.hasNext();
		}

		@Override
		public E next() {
			this.curr = this.it.next();
			return this.curr;
		}

		@Override
		public void remove() {
			synchronized(BlockingUniqueQueue.this) {
				this.it.remove();
				BlockingUniqueQueue.this.datas.remove(this.curr);
			}
		}
	}

}
