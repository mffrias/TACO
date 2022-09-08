package ase2016.introclass.median;

public class introclass_36d8008b_000 {

    public introclass_36d8008b_000() {
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
    	int d;
    	if (a < b && a > c || a > b && a < c) { //mutGenLimit 1
            d = a; //mutGenLimit 1
        } else if (b > a && b < c || b < a && b > c) { //mutGenLimit 1
            d = b; //mutGenLimit 1
        } else {
            d = c; //mutGenLimit 1
        }
        return d;
    }
	
}
