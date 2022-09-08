package escj.test20;

/** This file contains some tests that the "non_null" pragma generates
 ** appropriate assumptions.
 **/

class NonNull {
	// the following would generate a compile-time error
	// /*@ non_null */ int x;

	/*@ non_null */Object o;

	// without the initializer, the following would generate a compile-time error
	static/*@ non_null */Object p = new NonNull();

	//@ ensures \result != null;
	Object m(int k, Object x, /*@ non_null */Object y,
	/*@ non_null */Object z) {
		if (x != null)
			return x;
		else if (k == 0)
			return y;
		else if (k == 1)
			return z;
		else if (k == 2)
			return o;
		else
			return p;
	}
}
