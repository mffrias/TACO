package ase2016.introclass.median;

public class introclass_90a14c1a_000 {

    public introclass_90a14c1a_000() {
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
    	if ((a > b) && (a > c) && (b > c)) { //mutGenLimit 1
            return b;
        } else if ((a > b) && (a > c) && (c > b)) { //mutGenLimit 1
            return c;
        } else if ((b > a) && (b > c) && (c > a)) { //mutGenLimit 1
            return c;
        } else if ((b > a) && (b > c) && (a > c)) { //mutGenLimit 1
            return a;
        } else if ((c > a) && (c > b) && (a > b)) { //mutGenLimit 1
            return a;
        } else if ((c > a) && (c > b) && (b > a)) { //mutGenLimit 1
            return b;
        }
    	return 0; //mutGenLimit 1
    }
	
}
