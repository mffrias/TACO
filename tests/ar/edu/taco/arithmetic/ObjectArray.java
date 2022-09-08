package ar.edu.taco.arithmetic;

public class ObjectArray {

	/**
	 * @Ensures throw==null;
	 */
	public Object array_constructor(int j) {
		Object ret;
		Object[] a = new Object[j];
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_initialization() {
		Object[] a = new Object[10];
		if (a[0]!=null)
		  return this;
		else
		  return null;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_length(Object[] a) {
		Object ret;
		if (a != null) {
			int l = a.length;
		}
		return this;
	}

	/**
	 * @Ensures throw==null;
	 */
	public Object array_null_length(Object[] a) {
		int l = a.length;
		return this;
	}

	/**
	 * @Requires a==null;
	 * @Ensures throw==null;
	 */
	public Object array_null_read_literal(Object[] a) {
		Object l = a[0];
		return this;
	}

	/**
	 * @Requires a==null;
	 * @Ensures throw==null;
	 */
	public Object array_null_read_variable(Object[] a) {
		int k = 0;
		Object l = a[k];
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_read(Object[] arg) {
		Object j;
		
		if (arg!=null && arg.length > 0) {
			j = arg[0];
		}
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object array_write(Object[] arg, Object j) {

		if (arg!=null && arg.length > 0) {
			arg[0] = j;
		}
		return this;
	}

	/**
	 * @Requires a!=null;
	 * @Ensures throw==null;
	 */
	public Object arrays_read_out_of_bounds(Object[] a) {
		int i = 5;
		Object l = a[i];
		return this;
	}

	/**
	 * @Ensures return==null;
	 */
	public Object arrays_read_write(Object[] a_arg, Object[] b_arg) {
		if (a_arg!=null && a_arg.length > 0 && b_arg!=null && b_arg.length > 0) {
			a_arg[0] = b_arg[0];
		}
		return this;
	}

	/**
	 * @Requires a!=null;
	 * @Ensures throw==null;
	 */
	public Object arrays_write_out_of_bounds(Object[] a, Object v) {
		int i = 5;
		a[i]=v;
		return this;
	}

}
