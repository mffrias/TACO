package forArielGodio;
public class ForActualBugs {
	

	
	 //@ requires n != 2;
	  //@ requires 0 <= n && n <= 5;
	  //@ ensures n == 0 ==> \result == 1L;
	  //@ ensures n == 1 ==> \result == 1L;
	  //@ ensures n == 2 ==> \result == 2L;
	  //@ ensures n == 3 ==> \result == 6L;
	  //@ ensures n == 4 ==> \result == 24L;
	  //@ ensures n == 5 ==> \result == 120L;
	  //@ signals (Exception e) false;
	  public /*@ pure @*/ long factorial(int n) {
	    int c;
	    long fact = 1;

	    if (n == 0) {
	      return fact;
	    }

	    for (c = 1; c <= n; c++) {
	      fact = fact / c; // fact = fact*c;
	    }

	    return n;
	  }
}