package forArielGodio.fibonacci.bug03;
public class Fibonacci {
 
	private /*@spec_public @*/ long fib[];

   	//@ invariant 2 <= fib.length && fib.length <= 93; // 93 < size ==> Long Overflow 

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
	  @ signals (IllegalArgumentException e) (2>size || size > 93);
	  @*/
	public Fibonacci(int size) {
		if (2 <= size && size <= 93) {//if (2 <= size && size <= 93) {
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
	//@ ensures (\forall int i; 2 <= i && i < fib.length; fib[i] == fib[i-1] + fib[i-2]); 
	//@ ensures (\forall int i; 2 <= i && i < fib.length; (\forall int j; 2 <= j && j < i; fib[j] < fib[i]));
    //@ signals (Exception e) false;
	public void fibCompute() {
		int index = 2;
		while (index < fib.length) {
			fib[index] = fib[index - 2] + fib[index - 1];
			index++;
		}
	}
}