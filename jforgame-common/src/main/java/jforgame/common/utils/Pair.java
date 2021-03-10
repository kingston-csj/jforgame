package jforgame.common.utils;

/**
 * 两个对象组合起来的点对，一般用于需要返回两个返回值的方法
 * @author kinson
 */
public class Pair<F, S> {

	private final  F first;

	private final S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

}
