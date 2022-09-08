package ase2016.introclass.median;

public class introclass_6aaeaf2f_000 {

    public introclass_6aaeaf2f_000() {
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
    	if (((a > b) && (a < c)) || ((a < b) && (a > c))) { //mutGenLimit 1
            median = a;
        } else if (((b > a) && (c > b)) || ((b < a) && (b > c))) { //mutGenLimit 1
            median = b;
        } else {
            median = c; //mutGenLimit 1
        }
        return median; //mutGenLimit 1
    }
	
}
