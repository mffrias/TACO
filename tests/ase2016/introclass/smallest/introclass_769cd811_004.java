package ase2016.introclass.smallest;

public class introclass_769cd811_004 {
	
	public introclass_769cd811_004() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int x;
		if (a >= b) { //mutGenLimit 1
            x = b;
        } else {
            x = a; //mutGenLimit 1
        }
        if (b >= c) { //mutGenLimit 1
            x = c;
        }
        if (c >= d) { //mutGenLimit 1
            x = d;
        }
        return a; //mutGenLimit 1
	}
	
}
