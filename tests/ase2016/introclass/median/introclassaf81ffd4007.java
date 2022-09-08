package ase2016.introclass.median;

public class introclassaf81ffd4007 {

    public introclassaf81ffd4007() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)));
    @ ensures ((\old(a)!=\old(b) || \old(a)!=\old(c)) ==> ( ((\old(a)==\old(b)) ==> (\result == \old(a))) && ((\old(b)==\old(c)) ==> (\result ==\old(b)))));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result>n));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
    	int median = (a + b + c) / 3;
        int comp_fir = abs (a - median); //mutGenLimit 1
        int comp_sec = abs (b - median); //mutGenLimit 1
        int comp_thi = abs (c - median); //mutGenLimit 1
        if (comp_fir < comp_sec && comp_fir < comp_thi) { //mutGenLimit 1
            return a;
        } else if (comp_sec < comp_fir && comp_sec < comp_thi) { //mutGenLimit 1
            return b;
        } else if (comp_thi < comp_fir && comp_thi < comp_sec) { //mutGenLimit 1
            return c;
        }
        return 0; //mutGenLimit 1
    }
    
    public int abs(int value) {
    	if (value < 0) return -value;
    	return value;
    }
    
    
    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)));
    @ ensures ((\old(a)!=\old(b) || \old(a)!=\old(c)) ==> ( ((\old(a)==\old(b)) ==> (\result == \old(a))) && ((\old(b)==\old(c)) ==> (\result ==\old(b)))));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result>n));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int ianmedFix( int a, int b, int c ) {
    	int median = (a + b + c) / 3;
        int comp_fir = abs (a - median); //mutGenLimit 1
        int comp_sec = abs (b - median); //mutGenLimit 1
        int comp_thi = abs (c - median); //mutGenLimit 1
        if (((comp_fir < comp_sec) && (comp_fir < comp_thi))) { //mutGenLimit 1
            return a;
        } else if (((comp_sec <= comp_fir) && (comp_sec < comp_thi))) { //mutGenLimit 1
            return b;
        } else if (((comp_thi < comp_fir) && (comp_thi < comp_sec))) { //mutGenLimit 1
            return c;
        }
        return 0; //mutGenLimit 1
    }
    
    
	
}
