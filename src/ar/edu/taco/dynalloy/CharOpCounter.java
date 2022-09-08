package ar.edu.taco.dynalloy;

public class CharOpCounter {

	// char counters
	private int java_primitive_char_value_add=0;
	private int java_primitive_char_value_sub=0;
	private int java_primitive_char_value_decr_incr=0;
	private int java_primitive_char_value_narrowing_cast=0;

	
	public void inc_java_primitive_char_value_narrowing_cast() {
		java_primitive_char_value_narrowing_cast++;
	}
	
	public void inc_java_primitive_char_value_add() {
		java_primitive_char_value_add++;
	}

	public void inc_java_primitive_char_value_sub() {
		java_primitive_char_value_sub++;
	}
	
	public void inc_java_primitive_char_value_decr_incr() {
		java_primitive_char_value_decr_incr++;
	}


	public int add_count() {
		return java_primitive_char_value_add;
	}

	public int sub_count() {
		return java_primitive_char_value_sub;
	}

	public int dec_incr_count() {
		return java_primitive_char_value_decr_incr;
	}
	
	public int narrowing_cast_count() {
		return java_primitive_char_value_narrowing_cast;
	}

	
}
