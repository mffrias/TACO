package ase2016.introclass.smallest;

public class introclass_9013bd3b_000 {
	
	public introclass_9013bd3b_000() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		if ((a < b) && (a < c) && (a < d)) { //mutGenLimit 1
            return a;
        }
        if ((b < a) && (b < c) && (b < d)) { //mutGenLimit 1
            return b;
        }
        if ((c < a) && (c < b) && (c < d)) { //mutGenLimit 1
            return c;
        }
        if ((d < c) && (d < b) && (d < a)) { //mutGenLimit 1
            return d;
        }
        return 0; //mutGenLimit 1
	}
	
}
