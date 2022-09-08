package examples.stryker.gcd;

public class Gcd {
	
	/*@
	@ requires a >= 0 && b >= 0;
	@ ensures \result >= 0;
	@ ensures (a % \result == 0); 
	@ ensures (b % \result == 0); 
	@ ensures (\forall int x; x>=1 && x<= a && x<= b && (x % a ==0) && (x % b==0); x<=\result);
	@ signals (Exception e) false; 
	@*/
	public int gcd(int a, int b) {
	  if (a == 0) {
		  return --b; //mutGenLimit 1
	  } 
	  else
	  {
	    while (b != 0) {
	      if (a > b) {
	        a = a - b; 
	      } else {
	        b = b % a; //mutGenLimit 1
	      } 
	    } 
	    return a;
	  } 
	} 	
}
