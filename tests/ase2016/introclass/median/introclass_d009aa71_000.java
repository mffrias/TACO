package ase2016.introclass.median;

public class introclass_d009aa71_000 {

    public introclass_d009aa71_000() {
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
    	if ((a < c) && (a > b)) { //mutGenLimit 5
            return a; //mutGenLimit 5
        } else if ((a < b) && (a > c)) { 
            return a;
        }
        if ((b < a) && (b > c)) { 
            return b;
        } else if ((b > a) && (b < c)) { 
            return b;
        }
        if ((c > a) && (c < b)) { 
            return c;
        } else if ((c < a) && (c > b)) { 
            return c;
        }
        return 0; //mutGenLimit 1
    }
	
}
