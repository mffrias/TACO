package ase2016.introclass.median;

public class introclass_d120480a_000 {

    public introclass_d120480a_000() {
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
    	int median, temp;
    	if (a >= b) { //mutGenLimit 1
            temp = b; //mutGenLimit 1
            b = a; //mutGenLimit 1
            a = temp; //mutGenLimit 1
        }
        if (a < c) { //mutGenLimit 1
            median = b; //mutGenLimit 1
        } else if (b > c) { //mutGenLimit 1
            median = a; //mutGenLimit 1
        } else {
            median = c; //mutGenLimit 1
        }
        return median;
    }
	
}
