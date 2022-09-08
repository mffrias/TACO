package ar.edu.taco.arithmetic;

/**
 * @author jgaleotti
 */
public class ArraysWithArithmetic {

	/**
	 * @Ensures return==null;
	 */
	public Object array_length(int[] a) {
		Object ret;
		if (a != null) {
			int l = a.length;
		}
		return this;
	}

	/**
	 * @Ensures throw==null;
	 */
	public Object array_constructor(int j) {
		Object ret;
		int[] a = new int[j];
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_read(int[] arg) {
		int j;
		
		if (arg!=null && arg.length > 0) {
			j = arg[0];
		}
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_write(int[] arg) {
		int j = 12;
		if (arg!=null && arg.length > 0) {
			arg[0] = j;
		}
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object arrays_read_write(int[] a_arg, int[] b_arg) {
		int j = 12;
		if (a_arg!=null && a_arg.length > 0 && b_arg!=null && b_arg.length > 0) {
			a_arg[0] = b_arg[0];
		}
		return this;
	}

	/**
	 * @Ensures throw==null;
	 */
	public Object array_null_length(int[] a) {
		int l = a.length;
		return this;
	}

	/**
	 * @Requires a==null;
	 * @Ensures throw==null;
	 */
	public Object array_null_read_variable(int[] a) {
		int k = 0;
		int l = a[k];
		return this;
	}

	/**
	 * @Requires a==null;
	 * @Ensures throw==null;
	 */
	public Object array_null_read_literal(int[] a) {
		int l = a[0];
		return this;
	}


	/**
	 * @Requires a!=null;
	 * @Ensures throw==null;
	 */
	public Object arrays_read_out_of_bounds(int[] a) {
		int i = 5;
		int l = a[i];
		return this;
	}

	/**
	 * @Requires a!=null;
	 * @Ensures throw==null;
	 */
	public Object arrays_write_out_of_bounds(int[] a) {
		int i = 5;
		int v = 4;
		a[i]=v;
		return this;
	}


	/**
	 * @Ensures return==null;
	 */
	public Object array_initialization() {
		int[] a = new int[10];
		if (a[0]!=0)
		  return this;
		else
		  return null;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object big_array() {
		int[] a = new int[500000];
		if (a[499999]==0)
		  return this;
		else
		  return null;
	}
	
}
