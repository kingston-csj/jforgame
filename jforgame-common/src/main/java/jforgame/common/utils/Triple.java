package jforgame.common.utils;

/**
 * 三元结构体
 *
 * @param <F>
 * @param <S>
 * @param <T>
 */
public class Triple<F, S, T> {

	private F first;
	
	private S second;
	
	private T third;
	
	public Triple(F first, S second, T third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	public T getThird() {
		return third;
	}

	@Override
	public String toString() {
		return "Triple [first=" + first + ", second=" + second + ", third=" + third + "]";
	}
	
}
