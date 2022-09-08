package ase2016.introclass.median;

public class introclass_15cb07a7_003 {

    public introclass_15cb07a7_003() {
    }

    /*@
    @ requires true;
    @ ensures ((\result == a) || (\result == b) || (\result == c));
    @ ensures ( (a == b) ==> ((\result == a) || (\result == b) ) );
    @ ensures ( (b == c) ==> ((\result == b) || (\result == c) ) );
    @ ensures ( (a == c) ==> ((\result == a) || (\result == c) ) );
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result>n));
    @ ensures ((a!=b && a!=c && b!=c) ==> (\exists int n; (n == a) || (n == b) || (n == c); \result<n));
    @ signals (RuntimeException e) false;
    @
    @*/
    public int median( int a, int b, int c ) {
        int cmp1, cmp2, med;
        if (a <= b) {
            cmp1 = a;
        } else {
            cmp1 = b; //mutGenLimit 
        }
        if (b <= c) { //mutGenLimit 0
            cmp2 = b; //mutGenLimit 0
        } else {
            cmp2 = c;
        }
        if (cmp1 >= cmp2) {
            med = cmp1;
        } else {
            med = cmp2;
        }
        return med;
    }

}
