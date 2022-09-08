package forArielGodio.fibonacci.bug02;
public class Fibonacci {

	public long fib[];

	/*@ 	
	  @ ensures (2 <= \old(size) && \old(size) <= 93) ==>
	  @ 	(fib[0] == 0 && fib[1] == 1 && (\forall int i; 2 <= i && i < fib.length; fib[i] == 0));
	  @ signals (IllegalArgumentException e) \old(size) < 2 || 93 < \old(size);
	  @*/
	public Fibonacci(int size) {
		if (2 <= size && size < 93) {//if (2 <= size && size <= 93) {
			fib = new long[size];	
			fib[0] = 0;
			fib[1] = 1;
		} else {
			throw new IllegalArgumentException();
		}
	}

}