package ar.edu.taco.dynalloy;

public class IntegerOpCounter {

	// integer counters
	private int java_primitive_integer_value_add = 0;
	private int java_primitive_integer_value_sub = 0;
	private int java_primitive_integer_value_sshr = 0;
	private int java_primitive_integer_value_mul = 0;
	private int java_primitive_integer_value_div_rem = 0;
	private int java_primitive_integer_value_cast_from_char = 0;
	private int java_primitive_integer_value_narrowing_cast = 0;

	public void inc_add() {
		java_primitive_integer_value_add++;
	}

	public void inc_sub() {
		java_primitive_integer_value_sub++;
	}

	public void inc_narrowing_cast() {
		java_primitive_integer_value_narrowing_cast++;
	}

	public void inc_sshr() {
		java_primitive_integer_value_sshr++;

	}

	public void inc_mul() {
		java_primitive_integer_value_mul++;

	}

	public void inc_div_rem() {
		java_primitive_integer_value_div_rem++;
	}
	
	public void inc_cast_from_char() {
		java_primitive_integer_value_cast_from_char++;
	}


	public int add_count() {
		return java_primitive_integer_value_add;
	}

	public int sub_count() {
		return java_primitive_integer_value_sub;
	}

	public int sshr_count() {
		return java_primitive_integer_value_sshr;
	}

	public int mul_count() {
		return java_primitive_integer_value_mul;
	}

	public int div_rem_count() {
		return java_primitive_integer_value_div_rem;
	}
	
	public int cast_char_count() {
		return java_primitive_integer_value_cast_from_char;
	}
	
	public int narrowing_cast_count() {
		return java_primitive_integer_value_narrowing_cast;
	}


}
