package ase2016.introclass.smallest;

public class introclass_48b82975_001 {
	
	public introclass_48b82975_001() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int r;
		if (a < b && a < c && a < d) { //mutGenLimit 1
            r = a;
        } else if (b < a && b < c && b < d) { //mutGenLimit 1
            r = b;
        } else if (c < a && c < b && c < d) { //mutGenLimit 1
            r = c;
        } else {
            r = d; //mutGenLimit 1
        }
        return r;
	}
	
}
