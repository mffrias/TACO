package ar.edu.taco.infer;

public class IntegerOrInfinity {
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