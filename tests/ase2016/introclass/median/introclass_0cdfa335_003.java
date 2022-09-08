package ase2016.introclass.median;

public class introclass_0cdfa335_003 {

    public introclass_0cdfa335_003() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == a) || (\result == b) || (\result == c));
    @ ensures ( (a == b) ==> ((\result == a) || (\result == b) ) );
    @ ensures ( (b == c) ==> ((\result == b) || (\result == c) ) );
    @ ensures ( (a == c) ==> ((\result == a) || (\result == c) ) );
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result>n));
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
        int theMedian;
        if (a >= b && a <= c || a >= c && a <= b) { //mutGenLimit 5
            theMedian = a; //mutGenLimit 1
        }
        if (b >= a && b <= c || b >= c && b <= a) { 
            theMedian = b; 
        } else {
            theMedian = c; 
        }
        return theMedian; 
    }
	
}
