package ase2016.introclass.smallest;

public class introclass_af81ffd4_000 {
	
	public introclass_af81ffd4_000() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int m = 0; //mutGenLimit 1
		int p = 0; //mutGenLimit 1
		int n = 0; //mutGenLimit 1
		if (a > b) { //mutGenLimit 1
            m = b; //mutGenLimit 1
        } else if (a < b) { //mutGenLimit 1
            m = a; //mutGenLimit 1
        }
        if (m > c) { //mutGenLimit 1
            n = c; //mutGenLimit 1
        } else if (m < c) { //mutGenLimit 1
            n = m; //mutGenLimit 1
        }
        if (n > d) { //mutGenLimit 1
            p = d; //mutGenLimit 1
        } else if (n < d) { //mutGenLimit 1
            p = n; //mutGenLimit 1
        }
        return p; //mutGenLimit 1
	}
	
}
