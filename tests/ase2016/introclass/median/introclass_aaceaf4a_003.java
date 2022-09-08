package ase2016.introclass.median;

public class introclass_aaceaf4a_003 {

    public introclass_aaceaf4a_003() {
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
    	if ((a > b) && (a < c)) { //mutGenLimit 3
            return a; //mutGenLimit 1
        }
        if ((a > c) && (a < b)) { 
            return a;
        }
        if ((b > a) && (b < c)) { 
            return b;
        }
        if ((b > c) && (b < a)) { 
            return b;
        }
        if ((c > b) && (c < a)) { 
            return c;
        }
        if ((c > a) && (c < b)) { 
            return c;
        }
        return 0; //mutGenLimit 1
    }
	
}
