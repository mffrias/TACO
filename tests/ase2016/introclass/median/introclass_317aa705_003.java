package ase2016.introclass.median;

public class introclass_317aa705_003 {

    public introclass_317aa705_003() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)));
    @ ensures ((\old(a)!=\old(b) || \old(a)!=\old(c)) ==> ( ((\old(a)==\old(b)) ==> (\result == \old(a))) && ((\old(b)==\old(c)) ==> (\result ==\old(b)))));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result>n));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
    	if ((b > a && b < c) || (b < a && b > c)) { //mutGenLimit 1
            return b; //mutGenLimit 1
        }
        if ((c > a && c < b) || (c < a && c > b)) { //mutGenLimit 1
            return c; //mutGenLimit 1
        }
        if ((a > b && a < c) || (a < b && a > c)) { //mutGenLimit 1
            return a; //mutGenLimit 1
        }
        return 0; //mutGenLimit 1
    }
	
}
