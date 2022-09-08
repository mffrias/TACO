package ar.edu.taco.infer;

public class PScopeArithmetic {

	//@ ensures \result==true;
	public boolean use_integer_operations() {
		int i = 5000; // 1
		int j = 100;  // 1
		int k1 = i + j; // 1
		int k2 = i - j; // 1
		int k3 = i * j; // 1
		int k4 = i / j; // 5
		int k5 = i / 2; // 1
		int k6 = i % j; // 5

		inner_use_int_operations(i, j);

		return false;
	}

	private void inner_use_int_operations(int i, int j) {
		int k1 = i / j; // 5
		int k2 = i * j; // 1
	}

	//@ ensures \result==true;
	public boolean use_long_operations() {
		long i = 5000L; // 1
		long j = 100L;  // 1
		long k1 = i + j; // 1
		long k2 = i - j; // 1
		long k3 = i * j; // 1
		long k4 = i / j; // 5
		long k5 = i / 2; // 6
		long k6 = i % j; // 5

		inner_use_long_operations(i, j);

		return false;
	}

	private void inner_use_long_operations(long i, long j) {
		long k1 = i / j; // 5
		long k2 = i * j; // 1
	}

	//@ ensures \result==true;
	public boolean use_float_operations() {
		float i = 5000f; //1
		float j = 100f;  //1
		float k1 = i + j; // 1
		float k2 = i - j; // 1
		float k3 = i * j; // 1
		float k4 = i / j; // 1

		return false;
	}

	//@ ensures \result==true;
	public boolean use_float_operations_unroll() {
		float i = 5000f;//1F
		float j = 100f; //1F
		int w  = 4; //1I
		while (w != 0) { //1I
			float k1 = i + j; //1F
			float k2 = i - j; //1F
			float k3 = i * j; //1F
			float k4 = i / j; //1F
			w--; //1I
		} //4F+1I
		return false;
	}

}
