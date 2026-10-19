package ar.edu.taco.dynalloy;

/**
 * Counts of the {@code long} operations of the method under analysis, part of an
 * {@link ArithmeticOpCounter}: additions and subtractions (those of a {@code char} and a
 * {@code long} included), multiplications, divisions and remainders (counted together), and
 * casts from {@code char} and from {@code int}. {@code ScopeInference} adds one
 * {@code JavaPrimitiveLongValue} per operation to the program scope, five per division or
 * remainder.
 */
public class LongOpCounter {

	private int java_primitive_long_value_add = 0;
	private int java_primitive_long_value_sub = 0;
	private int java_primitive_long_value_mul = 0;
	private int java_primitive_long_value_div_rem = 0;
	private int java_primitive_long_value_casts = 0;
	

	public void inc_java_primitive_long_value_add() {
		java_primitive_long_value_add++;
	}

	public void inc_java_primitive_long_value_sub() {
		java_primitive_long_value_sub++;
	}
	
	public void inc_java_primitive_long_value_mul() {
		java_primitive_long_value_mul++;
	}
	
	public void inc_java_primitive_long_value_div_rem() {
		java_primitive_long_value_div_rem++;
	}
	
	public void inc_java_primitive_long_value_casts() {
		java_primitive_long_value_casts++;
	}


	public int add_count() {
		return java_primitive_long_value_add;
	}

	public int sub_count() {
		return java_primitive_long_value_sub;
	}

	public int mul_count() {
		return java_primitive_long_value_mul;
	}

	public int div_rem_count() {
		return java_primitive_long_value_div_rem;
	}
	
	public int casts_count() {
		return java_primitive_long_value_casts;
	}

}
