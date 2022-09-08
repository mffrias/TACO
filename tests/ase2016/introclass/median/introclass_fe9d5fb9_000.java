package ase2016.introclass.median;

public class introclass_fe9d5fb9_000 {

    public introclass_fe9d5fb9_000() {
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
    	int small, big, median;
    	if (a > b) { //mutGenLimit 1
            small = b; //mutGenLimit 1
            big = a; //mutGenLimit 1
        } else {
            big = b; //mutGenLimit 1
            small = b; //mutGenLimit 1
        }
        if (c > big) { //mutGenLimit 1
            median = big; //mutGenLimit 1
        } else if (c < small) { //mutGenLimit 1
            median = small; //mutGenLimit 1
        } else {
            median = c; //mutGenLimit 1
        }
        return median;
    }
	
}
