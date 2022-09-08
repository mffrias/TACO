package ar.edu.taco.arithmetic;

/**
 * @author jgaleotti
 */
public class IntegerArithmetic {

	/***
	 * @Ensures return==null;
	 **/
	public Object lt_integers(int a, int b) {
		int c;
		if (a < b)
			c = a;
		else
			c = b;
		return this;
	}
	
	
	/***
	 * @Ensures return==null;
	 **/
	public Object operation_1(int x,int y, int z) {
		boolean goal;
		
		int l = x+y;
		
		if (l>z) {
			goal = true;
		} else {
			goal = false;
		}
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object lte_integers(int a, int b) {
		int c;
		if (a <= b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object add_integers(int a, int b) {
		int c;
		c = a + b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object gt_integers(int a, int b) {
		int c;
		if (a > b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object gte_integers(int a, int b) {
		int c;
		if (a >= b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Requires a>b;
	 * @Ensures return==null;
	 **/
	public Object spec_cmp_integers(int a, int b) {
		int c;
		if (a >= b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object eq_integers(int a, int b) {
		int c;
		if (a == b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object neq_integers(int a, int b) {
		int c;
		if (a != b)
			c = a;
		else
			c = b;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object copy_integer_literal() {
		int c;
		c = -1;
		return this;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object cmp_integer_literal(int a) {
		int c;
		if (a < -2)
			c = -1;
		else
			c = 3;
		return this;
	}

	/***
	 * @Ensures return!=a+b;
	 **/
	public int spec_add_integers(int a, int b) {
		int c;
		c = a + b;
		return c;
	}

	/*@
	  @ ensures \result!=a+b;
	  @*/
	public int spec_jml_add_integers(int a, int b) {
		int c;
		c = a + b;
		return c;
	}

	/***
	 * @Ensures return==0;
	 **/
	public int sub_integers() {
		int a = 1;
		int b = -1;
		int c;
		c = a - b;
		int ret_val;
		if (c == 2)
			ret_val = 1;
		else
			ret_val = 0;
		return ret_val;
	}
	
	/***
	 * @Ensures return!=a-b;
	 **/
	public int spec_sub_integers(int a, int b) {
		int c;
		c = a + b;
		return c;
	}

	
	/***
	 * @Ensures return==null;
	 **/
	public Object negate_integer(int a) {
		int c;
		c = -a;
		return this;
	}

	/***
	 * @Ensures return!=-a;
	 **/
	public int spec_negate_integer(int a) {
		int c;
		c = -a;
		return c;
	}

	/***
	 * @Ensures return==null;
	 **/
	public Object mul_integer(int a, int b) {
		int c;
		c = a *b;
		return this;
	}
	
	

	/***
	 * @Ensures return==null;
	 **/
	public Object div_integer(int a, int b) {
		int c;
		c = a /b;
		return this;
	}
	
	/***
	 * @Ensures return==null;
	 **/
	public Object rem_integer(int a, int b) {
		int c;
		c = a %b;
		return this;
	}
	
	
	/***
	 * @Ensures return==null;
	 **/
	public Object sub_literal(int a) {
		int c;
		c = a + 354514;
		
		if (c - 354514 == a) {
			return this;
		}
		
		return null;
	}
	
	/***
	 * @Ensures return==true;
	 **/
	public boolean add_overflow(int a) {
		int max_val = Integer.MAX_VALUE;
		
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
	 * @Ensures return==true;
	 **/
	public boolean sub_overflow(int a) {
		int prev_min_val = -2147483647;
		int min_val = prev_min_val - 1;
		
		boolean ret_val = true;
		if (a>0) {
			min_val =min_val- a;
			
			if (min_val>0) {
				ret_val = false;
			}
			
		}
		
		return ret_val;
	}
	
	/***
	 * @Ensures return==true;
	 **/
	public boolean mul_overflow(int a, int b) {
		boolean ret_val = true;

		int c;
		if (a>0 && b>0) {
			c = a*b;
			if (c<0) {
				ret_val = false;
			}
		}
		
		return ret_val;
	}
	
	/***
	 * @Ensures return==false;
	 **/
	public boolean mod_small_numbers() {
		int a = 21;
		int b = 4;
		int c = a % b;
		int d = 1;
		boolean ret_val = c == d;
		return ret_val;
	}
	
	
	/***
	 * @Ensures return==false;
	 **/
	public boolean mod_big_numbers() {
		int a = 24784;
		int b = -6562;
		int c = a % b;
		int d = 5098;
		boolean ret_val = c == d;
		return ret_val;
	}

	/***
	 * @Ensures return==false;
	 **/
	public boolean mul_small_numbers() {
		int a = 21;
		int b = 4;
		int c = a * b;
		int d = 84;
		boolean ret_val = c == d;
		return ret_val;
	}
	
	/***
	 * @Ensures return==false;
	 **/
	public boolean div_small_numbers() {
		int a = 21;
		int b = 4;
		int c = a / b;
		int d = 5;
		boolean ret_val = c == d;
		return ret_val;
	}
	
	/**
	 * @Ensures return==false;
	 */
	public boolean suffix_add(int a) {
		int b = a;
		int k = b;
		b++;
		int c = a +1;
		boolean ret_val = c==b;
		return ret_val;
	}
	

	//@ ensures \result==true;
	public boolean div_negative_and_positive() {
		int left = -1073741826;
		int right = 2;
		int correct_result = -536870913;

		int result =-536870913;
		result = left / right;
		boolean ret_val = result!=correct_result;
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean div_small_negative_and_positive() {
		int left = -2;
		int right = 2;
		int correct_result = -1;

		int result =-1;
		result = left / right;
		boolean ret_val = result!=correct_result;
		return ret_val;
	}
	
	
	//@ ensures \result==true;
	public boolean rem_small_negative_and_positive() {
		int left = 7;
		int right = -3;
		int correct_result = 1;

		int result = left % right;
		boolean ret_val = result!=correct_result;
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean rem_small_positive_and_negative() {
		int left = -7;
		int right = 3;
		int correct_result = 1;

		int result = left % right;
		boolean ret_val = result!=correct_result;
		return ret_val;
	}
}
