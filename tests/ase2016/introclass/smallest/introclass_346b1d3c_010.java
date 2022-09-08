package ase2016.introclass.smallest;

public class introclass_346b1d3c_010 {
	
	public introclass_346b1d3c_010() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		if (a <= b && a <= c && a <= d) { //mutGenLimit 1
            return a;
        } else if (b <= a && b <= c && b <= d) { //mutGenLimit 1
            return b;
        } else if (c <= a && c <= b && c <= d) { //mutGenLimit 1
            return c;
        } else if (d <= a && d <= b && d <= c) { //mutGenLimit 1
            return d;
        }
		return 0; //mutGenLimit 1
	}
	
}
