package ase2016.introclass.smallest;

public class introclass_88394fc0_003 {
	
	public introclass_88394fc0_003() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int smallest = a;
        if (a > b) { //mutGenLimit 1
            smallest = b;
        }
        if (b > c) { //mutGenLimit 1
            smallest = c;
        }
        if (c > d) { //mutGenLimit 1
            smallest = d; //mutGenLimit 1
        } else if (a > d) { //mutGenLimit 1
            smallest = d; //mutGenLimit 1
        }
        return smallest;
	}
	
}
