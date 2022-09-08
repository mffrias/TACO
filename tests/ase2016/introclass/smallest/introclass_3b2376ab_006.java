package ase2016.introclass.smallest;

public class introclass_3b2376ab_006 {
	
	public introclass_3b2376ab_006() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int small = a;
        if (b < a) { //mutGenLimit 1
            small = b; //mutGenLimit 1
        }
        if (c < b) { //mutGenLimit 1
            small = c; //mutGenLimit 1
        }
        if (d < c) { //mutGenLimit 1
            small = d;
        } else if (d < a) { //mutGenLimit 1
            small = d;
        }
        return small;
	}
	
}
