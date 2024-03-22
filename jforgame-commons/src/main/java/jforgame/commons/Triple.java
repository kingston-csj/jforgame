package jforgame.commons;

/**
 * 三元结构体
 */
public class Triple<F, S, T> {

	private final F first;

	private final S second;

	private final T third;

	public Triple(F first, S second, T third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	/**
	 *
	 * @return return first element of the triple
	 */
	public F getFirst() {
		return first;
	}

	/**
	 *
	 * @return return second element of the triple
	 */
	public S getSecond() {
		return second;
	}

	/**
	 *
	 * @return return third element of the triple
	 */
	public T getThird() {
		return third;
	}

	@Override
	public String toString() {
		return "Triple [first=" + first + ", second=" + second + ", third=" + third + "]";
	}

}
