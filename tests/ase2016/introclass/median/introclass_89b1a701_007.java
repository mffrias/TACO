package ase2016.introclass.median;

public class introclass_89b1a701_007 {

    public introclass_89b1a701_007() {
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
    	int m = 0; //mutGenLimit 1
    	if ((a >= b && a <= c) || (a >= c && a <= b)) { //mutGenLimit 1
            m = b;
        } else if ((b >= a && b <= c) || (b >= c && b <= a)) { //mutGenLimit 1
            m = b;
        } else if ((c >= a && c <= b) || (c >= b && c <= a)) { //mutGenLimit 1
            m = c;
        }
        return m; //mutGenLimit 1
    }
	
}
