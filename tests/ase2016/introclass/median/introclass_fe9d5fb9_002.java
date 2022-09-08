package ase2016.introclass.median;

public class introclass_fe9d5fb9_002 {

    public introclass_fe9d5fb9_002() {
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
    	int small, big, median;
    	if (a >= b) {
            small = b;
            big = a; 
        } else {
            big = b; //mutGenLimit 1
            small = b; //mutGenLimit 1
        }
        if (c >= big) {
            median = big;
        } else if (c <= small) { //mutGenLimit 1
            median = small;
        } else {
            median = c; 
        }
        return median;
    }
	
}
