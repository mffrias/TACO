package ase2016.introclass.median;

public class introclass_3cf6d33a_007 {

    public introclass_3cf6d33a_007() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)));
    @ ensures ( (a == b) ==> ((\result == a) || (\result == b) ) );
    @ ensures ( (b == c) ==> ((\result == b) || (\result == c) ) );
    @ ensures ( (a == c) ==> ((\result == a) || (\result == c) ) );
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result>n));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
    	if ((a > b && b > c) || (c > b && b > a)) { //mutGenLimit 4
            return b; //mutGenLimit 0
        } else if ((b > a && a > c) || (c > a && a > b)) {
            return a;
        } else if ((a > c && c > b) || (b > c && c > a)) {
            return c;
        }
    	return 0; //mutGenLimit 1
    }
	
}
