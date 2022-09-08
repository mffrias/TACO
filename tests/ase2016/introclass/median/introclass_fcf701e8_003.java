package ase2016.introclass.median;

public class introclass_fcf701e8_003 {

    public introclass_fcf701e8_003() {
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
    	while (a <= b && a <= c) { //mutGenLimit 1
            if (b < c) { //mutGenLimit 1
                return b;
            } else {
                return c; //mutGenLimit 1
            }
        }
        while (b <= a && b <= c) { //mutGenLimit 1
            if (a < c) { //mutGenLimit 1
                return a;
            } else {
                return c; //mutGenLimit 1
            }
        }
        while (c <= a && c <= b) { //mutGenLimit 1
            if (b < a) { //mutGenLimit 1
                return b;
            } else {
                return a; //mutGenLimit 1
            }
        }
        return 0; //mutGenLimit 1
    }
	
}
