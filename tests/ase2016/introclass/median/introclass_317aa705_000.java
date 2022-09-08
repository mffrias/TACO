package ase2016.introclass.median;

public class introclass_317aa705_000 {

    public introclass_317aa705_000() {
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
    	int temp1 = a;
        int temp2 = b;
    	if (a > b) { //mutGenLimit 1
            a = b;
            b = temp1; //mutGenLimit 1
        }
        if (b > c) { //mutGenLimit 1
            b = c;
            c = temp2; //mutGenLimit 1
        }
        if (a > b) { //mutGenLimit 1
            a = b;
            b = temp1; //mutGenLimit 1
        }
        return b; //mutGenLimit 1
    }
	
}
