package ar.edu.taco.dynalloy;

public class FloatOpCounter {

	// float counters
	private int java_primitive_float_value_mul=0;
	private int java_primitive_float_value_div=0;
	private int java_primitive_float_value_add=0;
	private int java_primitive_float_value_sub=0;

	public void inc_java_primitive_float_value_mul() {
		java_primitive_float_value_mul++;
	}
	
	public void inc_java_primitive_float_value_div() {
		java_primitive_float_value_div++;
	}
	
	public void inc_java_primitive_float_value_add() {
		java_primitive_float_value_add++;
	}

	public void inc_java_primitive_float_value_sub() {
		java_primitive_float_value_sub++;
	}

	public int add_count() {
		return java_primitive_float_value_add;
	}

	public int sub_count() {
		return java_primitive_float_value_sub;
	}

	public int mul_count() {
		return java_primitive_float_value_mul;
	}

	public int div_count() {
		return java_primitive_float_value_div;
	}
}
