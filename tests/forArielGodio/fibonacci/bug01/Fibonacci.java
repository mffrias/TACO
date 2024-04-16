package forArielGodio.fibonacci.bug01;
public class Fibonacci {
	private /*@ spec_public @*/ long fib[];
	//@ public invariant 2 <= fib.length && fib.length <= 93; // 93 < size ==> Long Overflow 

	//@ ensures fib[0] == 0 && fib[1] == 1;
	public Fibonacci() {
		fib = new long[2];
		fib[0] = 0;
		fib[1] = 1;
	}

	/*@ 
	  @ requires 2 <= size && size <= 93;
	  @ ensures fib[0] == 0 && fib[1] == 1;
	  @ ensures (\forall int i; 2 <= i && i < fib.length; fib[i] == 0);
	  @*/
	/*@ spec_public @*/ Fibonacci(int size) {
		if (2 <= size && size <= 93) {
			fib = new long[size];	
			fib[0] = 0;
			fib[1] = 1;
		} else {
			throw new IllegalArgumentException();
		}
	}

	//@ requires 0 <= index && index < fib.length;
	//@ ensures \result == fib[index];
	public /*@ pure @*/ long getFib(int index) {
		return fib[index];
	}

	//@ requires fib[0] == 0 && fib[1] == 1;
	//@ signals (Exception e) false;
	//@ signals (AssertionError e) true;
	public void fibCompute() {
		int index = 2;
		while (index < fib.length) {
			fib[index] = fib[index - 2] + fib[index + 1];//fib[index] = fib[index - 2] + fib[index - 1];
			index++;
		}
	}


	//@ requires true; 
	//@ ensures \result == 0;
	public int mfrias(int i) {
		if (i > 0)
			return i+1;
		return 0;
	}

	//	public static void main(String[] args) {
	//		Fibonacci f = new Fibonacci(6);
	//        f.fib[0] = 0L;
	//        f.fib[1] = 1L;
	//        f.fib[2] = 1L;
	//        f.fib[3] = 1L;
	//        f.fib[4] = 1L;
	//        f.fib[5] = 0L;
	//        f.fibCompute();
	//
	//	}
}