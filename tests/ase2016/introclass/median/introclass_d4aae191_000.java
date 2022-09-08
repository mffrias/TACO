package ase2016.introclass.median;

public class introclass_d4aae191_000 {

    public introclass_d4aae191_000() {
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
    	if ((a > b) && (b > c)) { //mutGenLimit 1
            return b;
        }
        if ((a > b) && (a > c)) { //mutGenLimit 1
            if (c > b) {
                return c;
            }
        }
        if ((b > a) && (a > c)) {
            return a;
        }
        if ((b > a) && (b > c)) { //mutGenLimit 1
            if (c > a) { 
                return c;
            }
        }
        if ((c > a) && (a > b)) { 
            return a;
        }
        if ((c > a) && (c > b)) { //mutGenLimit 1
            if (b > a) { 
                return b;
            }
        }
        return 0; //mutGenLimit 1
    }
	
}
