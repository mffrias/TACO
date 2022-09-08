package ase2016.introclass.smallest;

public class introclass_818f8cf4_002 {
	
	public introclass_818f8cf4_002() {
	}

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
	public int smallest(int a, int b, int c, int d) {
		int num_smallest;
		if ((a < b) && (a < c) && (a < d)) { //mutGenLimit 1
            num_smallest = a;
            return num_smallest;
        } else if ((b < a) && (b < c) && (b < d)) { //mutGenLimit 1
            num_smallest = b;
            return num_smallest;
        } else if ((c < a) && (c < b) && (c < d)) { //mutGenLimit 1
            num_smallest = c;
            return num_smallest;
        } else if ((d < a) && (d < b) && (d < c)) { //mutGenLimit 1
            num_smallest = a; //mutGenLimit 1
            return num_smallest;
        }
		return 0; //mutGenLimit 1
	}
	
}
