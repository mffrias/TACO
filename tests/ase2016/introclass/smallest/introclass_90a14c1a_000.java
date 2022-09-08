package ase2016.introclass.smallest;

public class introclass_90a14c1a_000 {
	
	public introclass_90a14c1a_000() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		if ((a < b) && (b < c) && (c < d)) { //mutGenLimit 1
            return a;
        }
        if ((b < a) && (a < c) && (c < d)) { //mutGenLimit 1
            return b;
        }
        if ((c < a) && (a < b) && (b < d)) { //mutGenLimit 1
            return c;
        }
        if ((d < a) && (a < b) && (b < c)) { //mutGenLimit 1
            return d;
        }
        return 0; //mutGenLimit 1
	}
	
}
