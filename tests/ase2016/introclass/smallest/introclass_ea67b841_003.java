package ase2016.introclass.smallest;

public class introclass_ea67b841_003 {
	
	public introclass_ea67b841_003() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int SN;
		if ((a < b) && (a < c) && (a < d)) { //mutGenLimit 1
            SN = a;
        } else if ((b < a) && (b < c) && (b < d)) { //mutGenLimit 1
            SN = b;
        } else if ((c < a) && (c < b) && (c < d)) { //mutGenLimit 1
            SN = c;
        } else {
            SN = d; //mutGenLimit 1
        }
        return SN;
	}
	
}
