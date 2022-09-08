package ase2016.introclass.median;

public class introclass_af81ffd4_004 {

    public introclass_af81ffd4_004() {
    }
    
    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)));
	@ ensures ( (a == b) ==> ((\result == a) || (\result == b) ) );
    @ ensures ( (b == c) ==> ((\result == b) || (\result == c) ) );
    @ ensures ( (a == c) ==> ((\result == a) || (\result == c) ) );
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result>n));
    @ ensures ((\old(a)!=\old(b) && \old(a)!=\old(c) && \old(b)!=\old(c)) ==> (\exists int n; (n == \old(a)) || (n == \old(b)) || (n == \old(c)); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
    	int median = (abs (a) + abs (b) + abs (c)) / 3; 
        int comp_fir = abs (a - median); 
        int comp_sec = abs (b - median); 
        int comp_thi = abs (c - median); 
        if (comp_fir < comp_sec && comp_fir < comp_thi) { //mutGenLimit 1
            return a;  //mutGenLimit 1
        } else if (comp_sec < comp_fir && comp_sec < comp_thi) {
            return b;
        } else if (comp_thi < comp_fir && comp_thi < comp_sec) {
            return c;
        }
        return 0; //mutGenLimit 1
    }
    
    public int abs(int value) {
    	if (value < 0) return -value;
    	return value;
    }
	
}
