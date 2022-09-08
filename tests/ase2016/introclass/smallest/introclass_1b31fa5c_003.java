package ase2016.introclass.smallest;

public class introclass_1b31fa5c_003 {
	
	public introclass_1b31fa5c_003() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
        if (a < b && a < c && a < d) { //mutGenLimit 3
        	return a;
        } else if (b < a && b < c && b < d) { //mutGenLimit 3
        	return b;
        } else if (c < a && c < b && c < d) { //mutGenLimit 3
        	return c;
        } else if (d < a && d < b && d < c) { //mutGenLimit 3
        	return d;
        }
		return 0;
	}
	
}
