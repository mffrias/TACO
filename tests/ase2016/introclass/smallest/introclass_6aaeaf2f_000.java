package ase2016.introclass.smallest;

public class introclass_6aaeaf2f_000 {
	
	public introclass_6aaeaf2f_000() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int tmp = a;
        if (a > b) { //mutGenLimit 1
            tmp = b;
        } else if (tmp > c) { //mutGenLimit 1
            tmp = c;
        } else if (tmp > d) { //mutGenLimit 1
            tmp = d;
        }
        return tmp;
	}
	
}
