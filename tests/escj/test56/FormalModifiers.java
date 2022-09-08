package escj.test56;

/**
 ** Test that we can put escjava modifiers after formal parameters
 **/

class FormalModifiers {
	//@ pure
	void foo(/*@non_null*/String x,
	/*@non_null*/Object z[],
	/*@non_null*/int[] q) {
	}

	//@ pure
	void bar(/*@non_null*/String a) {
	}

	//@ pure
	void b1(int b) {
	}

	//@ pure
	void b2(/*@non_null*/String b) {
	}
}
