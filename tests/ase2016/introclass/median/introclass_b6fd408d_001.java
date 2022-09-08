package ase2016.introclass.median;

public class introclass_b6fd408d_001 {

    public introclass_b6fd408d_001() {
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
    	int temp;
    	if (b < a) { //mutGenLimit 1
            temp = b; //mutGenLimit 1
            b = a; //mutGenLimit 1
            a = temp; //mutGenLimit 1
        }
        if ((c < b) && (c >= a)) { //mutGenLimit 1
            temp = b; //mutGenLimit 1
            b = c; //mutGenLimit 1
            c = temp; //mutGenLimit 1
        }
        return b; //mutGenLimit 1
    }
	
}
