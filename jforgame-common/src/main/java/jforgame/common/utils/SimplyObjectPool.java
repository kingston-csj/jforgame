package jforgame.common.utils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author kinson
 */
public class SimplyObjectPool <E extends MemoryObject> {
	
	private int capacity;
	
	private LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<>();
	
	public SimplyObjectPool(int capacity) {
		this.capacity = capacity;
	}
	
	@SuppressWarnings("unchecked")
	public E borrowObject(Class<? extends MemoryObject> prototype) throws InstantiationException, IllegalAccessException {
		E object = queue.poll();
		if (object != null) {
			return object;
		}
		return (E) prototype.newInstance();
	}
	
	public void returnObject(E object) {
		if (this.queue.size() < capacity) {
			object.release();
			this.queue.offer(object);
		}
	}
	

}
