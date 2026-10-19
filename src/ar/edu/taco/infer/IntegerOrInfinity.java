package ar.edu.taco.infer;

/**
 * A non-negative number of atoms, or infinity ({@link #INFINITY}), the value of a
 * {@link Scope} entry: an input scope is infinite when a signature can be reached through a
 * cycle ({@link GraphPathCounter}). {@code equals} is not overridden, so it compares identity:
 * comparing with {@link #INFINITY} works, but finite values must be compared through
 * {@link #int_value}, which is -1 for {@link #INFINITY}.
 */
public class IntegerOrInfinity {
	/**
	 * Creates a finite value.
	 *
	 * @param i the number, 0 or more
	 * @throws IllegalArgumentException if {@code i} is negative
	 */
	public IntegerOrInfinity(int i) {
		if (i < 0)
			throw new IllegalArgumentException();

		this.int_value = i;
	}

	private IntegerOrInfinity() {
		this.int_value = -1;
	}

	public final static IntegerOrInfinity INFINITY = new IntegerOrInfinity();

	public final int int_value;
	
	public String toString() {
		if (this==INFINITY)
			return "INF";
		else
			return String.valueOf(this.int_value);
			
	}
	
}