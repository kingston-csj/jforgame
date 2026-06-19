package jforgame.commons.ds;

/**
 * A pair of two objects combined, generally used for methods that need to return two values
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
