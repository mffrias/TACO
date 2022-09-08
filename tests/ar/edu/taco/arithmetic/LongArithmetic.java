package ar.edu.taco.arithmetic;

/**
 * 
 * @author jgaleotti
 * 
 */
public class LongArithmetic {

	/**
	 * @Ensures false;
	 */
	public long add_longs(long a, long b) {
		long ret = (a + b);
		return ret;
	}

	/**
	 * @Ensures false;
	 */
	public long sub_longs(long a, long b) {
		long ret = (a - b);
		return ret;
	}

	/***
	 * @Ensures false;
	 **/
	public long eq_longs(long a, long b) {
		long c;
		if (a == b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long gt_longs(long a, long b) {
		long c;
		if (a > b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long gte_longs(long a, long b) {
		long c;
		if (a >= b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long lt_longs(long a, long b) {
		long c;
		if (a < b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long lte_longs(long a, long b) {
		long c;
		if (a <= b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long neq_longs(long a, long b) {
		long c;
		if (a != b)
			c = a;
		else
			c = b;
		return c;
	}

	/***
	 * @Ensures false;
	 **/
	public long add_long_and_literal(long a) {
		a = a + 10;
		return a;
	}

	/**
	 * @Ensures false;
	 */
	public long mul_longs(long a, long b) {
		long ret = a * b;
		return ret;
	}

	/**
	 * @Ensures false;
	 */
	public long div_longs(long a, long b) {
		long ret = a / b;
		return ret;
	}

	/**
	 * @Ensures false;
	 */
	public long div_longs_stmt(long a, long b) {
		long ret;
		ret = a / b;
		return ret;
	}

	/**
	 * @Ensures false;
	 */
	public long rem_longs(long a, long b) {
		long ret;
		ret = a % b;
		return ret;
	}

	/**
	 * @Ensures false;
	 */
	public int puzzle_1(long i, long j) {
		long tmp1 = i * j;
		long tmp2 = tmp1 / j;
		int result;
		if (tmp2 == i) {
			result = 1;
		} else {
			result = 0;
		}
		return result;
	}

	/**
	 * @Ensures false;
	 */
	public int puzzle_2(long i, long j) {
		long tmp1 = i * j;
		long tmp2 = tmp1 / j;
		int result;
		if (tmp2 != i) {
			result = 1;
		} else {
			result = 0;
		}
		return result;
	}

	/***
	 * @Ensures return==true;
	 **/
	public boolean add_overflow(long a) {
		long max_val = Long.MAX_VALUE;

		boolean ret_val = true;
		if (a>0) {
			max_val =max_val + a;
			
			if (max_val<0) {
				ret_val = false;
			}
			
		}
		
		return ret_val;
	}
	
	

	/***
	 * @Ensures return==false;
	 **/
	public boolean sub_overflow() {
		long a = -9223372036854775807L;
		long b = a - 1L;
		long c = b - 1L;
		long d = 9223372036854775807L;
		boolean ret_val = (c==d);
		return ret_val;
	}

}
