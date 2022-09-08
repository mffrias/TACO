package ase2016.introclass.median;

public class introclass_3b2376ab_006 {

    public introclass_3b2376ab_006() {
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
        if (a <= b) { //mutGenLimit 3
            small = a; //mutGenLimit 0
            if (small > c) { //mutGenLimit 0
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
            } else if (c > a) {
                return a;
            } else {
                return c;
            }
        }		
	}
	
}
