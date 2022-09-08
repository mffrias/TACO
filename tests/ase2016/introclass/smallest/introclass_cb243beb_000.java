package ase2016.introclass.smallest;

public class introclass_cb243beb_000 {
	
	public introclass_cb243beb_000() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int min;
		if (a <= b) { //mutGenLimit 1
            min = a;
        } else {
            min = b; //mutGenLimit 1
        }
        if (min >= c) { //mutGenLimit 1
            min = c;
        }
        if (min >= d) { //mutGenLimit 1
            min = d;
        } else {
            return min; //mutGenLimit 1
        }
        return 0; //mutGenLimit 1
	}
	
}
