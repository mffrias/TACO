package ase2016.introclass.median;

public class introclass_48b82975_000 {

    public introclass_48b82975_000() {
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
    public int median( int a, int b, int c ) { //Gives unsat because it's an error with the translation
    	if ((a <= b && b <= c) || (c <= b && b <= a)) { //mutGenLimit 1
            return b; //mutGenLimit 0
        }
        if ((b <= a && a <= c) || (c <= a && a <= b)) { //mutGenLimit 0
            return a;
        } else {
            return c; //mutGenLimit 0
        }
    }
	
}
