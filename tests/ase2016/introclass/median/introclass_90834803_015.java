package ase2016.introclass.median;

public class introclass_90834803_015 {

    public introclass_90834803_015() {
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
    	if ((b >= a && a >= c) || (c <= a && a <= b) || (a < b && a < c)) { //mutGenLimit 1
            return a;
        } else if ((a >= b && b >= c) || (a <= b && b <= c) || (b < c && b < a)) { //mutGenLimit 1
            return b;
        } else if ((a >= c && c >= b) || (a <= c && c <= b) || (c < a && c < b)) { //mutGenLimit 1
            return c;
        }
    	return 0; //mutGenLimit 1
    }
	
}
