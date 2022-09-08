package ase2016.introclass.smallest;

public class introclass_769cd811_002 {
	
	public introclass_769cd811_002() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int res;
		if (a >= b) { //mutGenLimit 1
            res = b;
        } else {
            res = a; //mutGenLimit 1
        }
        if (b >= c) { //mutGenLimit 1
            res = c;
        }
        if (c >= d) { //mutGenLimit 1
            res = d;
        }
        return res;
	}
	
}
