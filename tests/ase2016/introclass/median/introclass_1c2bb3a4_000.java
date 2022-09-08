package ase2016.introclass.median;

public class introclass_1c2bb3a4_000 {

    public introclass_1c2bb3a4_000() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == a) || (\result == b) || (\result == c));
    @ ensures ( (a == b) ==> ((\result == a) || (\result == b) ) );
    @ ensures ( (b == c) ==> ((\result == b) || (\result == c) ) );
    @ ensures ( (a == c) ==> ((\result == a) || (\result == c) ) );
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result>n));
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
	public int median(int a, int b, int c) {
        if (a == b) { 
        	return a; 
        }
        if (a == c) { //mutGenLimit 5
            return a; //mutGenLimit 4
        }
        if (b == c) { 
            return b; 
        }
        return 0; //mutGenLimit 1
	}
	
}
