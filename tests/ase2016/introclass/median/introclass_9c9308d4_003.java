package ase2016.introclass.median;

public class introclass_9c9308d4_003 {

    public introclass_9c9308d4_003() {
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
    	int median;
    	if (a >= b || a >= c) { //mutGenLimit 1
            if (b >= c && a >= b) { //mutGenLimit 1
                median = b;
            } else {
                median = a; //mutGenLimit 1
            }
        } else if (b >= c) { //mutGenLimit 1
            median = c;
        } else {
            median = b; //mutGenLimit 1
        }
    	return median;
    }
	
}
