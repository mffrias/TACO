package ase2016.introclass.smallest;

public class introclass_95362737_009 {
	
	public introclass_95362737_009() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		if (a > b) { //mutGenLimit 1
            if (b > c) { //mutGenLimit 1
                if (c > d) { //mutGenLimit 1
                    return d;
                } else {
                    return c; //mutGenLimit 1
                }
            } else if (b > d) { //mutGenLimit 1
                return d;
            } else {
                return b; //mutGenLimit 1
            }
        } else if (a > c) { //mutGenLimit 1
            if (c > d) { //mutGenLimit 1
                return d;
            } else {
                return c; //mutGenLimit 1
            }
        } else if (a > d) { //mutGenLimit 1
            return d;
        } else {
            return a; //mutGenLimit 1
        }
	}
	
}
