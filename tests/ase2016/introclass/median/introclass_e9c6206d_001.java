package ase2016.introclass.median;

public class introclass_e9c6206d_001 {

    public introclass_e9c6206d_001() {
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
    	if ((a < b && b < c) || (c < b && b < a)) { //mutGenLimit 1
            return b;
        } else if ((b < a && a < c) || (c < a && a < b)) { //mutGenLimit 1
            return a;
        } else if ((a < c && c < b) || (b < c && c < a)) { //mutGenLimit 1
            return c;
        }
    	return 0; //mutGenLimit 1
    }
	
}
