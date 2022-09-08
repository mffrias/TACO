package ase2016.introclass.median;

public class introclass_fcf701e8_000 {

    public introclass_fcf701e8_000() {
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
    	while (a < b && a < c) { //mutGenLimit 2
            if (b < c) {
                return b;
            } else {
                return c;
            }
        }
        while (b < a && b < c) { //mutGenLimit 2
            if (a < c) {
                return a;
            } else {
                return c;
            }
        }
        while (c < a && c < b) { //mutGenLimit 2
            if (b < a) {
                return b;
            } else {
                return a;
            }
        }
		return 0;
    }
	
}
