package forArielGodio.fibonacci.bug01;
public class Fibonacci {
	private /*@ spec_public @*/ long fib[];
	//@ public invariant 2 <= fib.length && fib.length <= 93; // 93 < size ==> Long Overflow 



	//@ requires 0 <= index && index < fib.length;
	//@ ensures \result == fib[index];
	public /*@ pure @*/ long getFib(int index) {
		return fib[index];
	}

	//@ requires fib[0] == 0 && fib[1] == 1;
	//@ signals (Exception e) false;
	//@ signals (AssertionError) true;
	public void fibCompute() {
		int index = 2;
		while (index < fib.length) {
			fib[index] = fib[index - 2] + fib[index + 1];//fib[index] = fib[index - 2] + fib[index - 1];
			index++;
		}
	}

}