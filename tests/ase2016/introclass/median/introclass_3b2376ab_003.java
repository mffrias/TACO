package ase2016.introclass.median;

public class introclass_3b2376ab_003 {

    public introclass_3b2376ab_003() {
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
	public int median(int a, int b, int c) {
		int small;
        if (a < b) { 
            small = a;
            if (small > c) {
                return a;
            } else if (c > b) {
                return b;
            } else {
                return c;
            }
        } else {
            small = b;
            if (small > c) { 
                return b;
            } else if (c > a) { //mutGenLimit 1
                return c; //mutGenLimit 1
            } else {
                return a; //mutGenLimit 1
            }
        }
	}
	
}
