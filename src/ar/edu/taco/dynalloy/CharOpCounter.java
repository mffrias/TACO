package ar.edu.taco.dynalloy;

/**
 * Counts of the {@code char} operations of the method under analysis, part of an
 * {@link ArithmeticOpCounter}. {@code ScopeInference} adds one {@code JavaPrimitiveCharValue}
 * per addition, subtraction and narrowing cast to the program scope. Only the narrowing casts
 * to {@code char} are ever counted: {@code ArithmeticOpCollector} calls none of the other
 * {@code inc_} methods (additions and subtractions of {@code char}s are counted as
 * {@code int} or {@code long} operations), so {@link #add_count()} and {@link #sub_count()}
 * are always 0, and nothing reads {@link #dec_incr_count()}.
 */
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
