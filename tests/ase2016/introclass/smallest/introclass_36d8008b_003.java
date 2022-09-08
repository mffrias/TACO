package ase2016.introclass.smallest;

public class introclass_36d8008b_003 {
	
	public introclass_36d8008b_003() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int e;
		if (a < b && a < c && a < d) { //mutGenLimit 1
            e = a;
        } else if (b < a && b < c && b < d) { //mutGenLimit 1
            e = b;
        } else if (c < a && c < b && c < d) { //mutGenLimit 1
            e = c;
        } else {
            e = d; //mutGenLimit 1
        }
        return e;
	}
	
}
