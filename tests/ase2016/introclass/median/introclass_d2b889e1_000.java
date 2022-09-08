package ase2016.introclass.median;

public class introclass_d2b889e1_000 {

    public introclass_d2b889e1_000() {
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
    	int median = 0; //mutGenLimit 1
    	if ((a <= b && b < c) || (c <= b && b <= a)) { //mutGenLimit 1
            median = b;
        } else if ((b <= c && c <= a) || (a <= c && c <= b)) { //mutGenLimit 1
            median = b;
        } else if ((c <= a && a <= b) || (b <= a && a <= c)) { //mutGenLimit 1
            median = c;
        }
        return median;
    }
	
}
