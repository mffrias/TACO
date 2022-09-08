package ar.edu.taco.arithmetic;

public class FloatArithmetic {

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean eq_floats(float i, float j) {
		boolean ret_val;
		if (i==j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean neq_floats(float i, float j) {
		boolean ret_val;
		if (i==j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * @Ensures return==false;
	 */
	public boolean infinity_float(float i) {
		boolean ret_val;
		if (i!=i) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean gt_floats(float i, float j) {
		boolean ret_val;
		if (i>j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean gte_floats(float i, float j) {
		boolean ret_val;
		if (i>=j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean lt_floats(float i, float j) {
		boolean ret_val;
		if (i<j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean lte_floats(float i, float j) {
		boolean ret_val;
		if (i<=j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}
	
	/**
	 * 
	 * @Ensures false;
	 */
	public boolean float_literal(float i) {
		boolean ret_val;
		float j = 1.0f;
		if (i==j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}
	
	
	/**
	 * 
	 * @Ensures false;
	 */
	public boolean float_max_value(float i) {
		boolean ret_val;
		float j = Float.MAX_VALUE;
		if (i==j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * 
	 * @Ensures false;
	 */
	public boolean float_positive_infinity(float i) {
		boolean ret_val;
		float j = Float.POSITIVE_INFINITY;
		if (i==j) {
			ret_val = true;
		}else {
			ret_val = false;
		}
		return ret_val;
	}

	/**
	 * @Ensures false;
	 */
	public void many_types(int i, long l, float j) {
		int ii = i +1 ;
		long ll = l + 1;
		float jj = j;
	}
	
	/**
	 * @Ensures false;
	 */
	public void float_mul(float f) {
		float k;
		k = f * 1.f;
	}
	
	/**
	 * @Ensures false;
	 */
	public void float_div(float f) {
		float k;
		k = f / 1.f;
	}
	
	/**
	 * @Ensures false;
	 */
	public void float_add(float f1, float f2) {
		float k;
		k = f1 + f2;
	}
	
	/**
	 * @Ensures false;
	 */
	public void float_sub(float f1, float f2) {
		float k;
		k = f1 - f2;
	}
}
