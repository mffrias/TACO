package ase2016.introclass.grade;

public class introclass_af81ffd4_001 {

	public introclass_af81ffd4_001() {
	}

    /*@
    @ requires (a > b && b > c && c > d);
    @ ensures ((\old(score) >= \old(a)) <==> (\result == 1));
    @ ensures ((\old(score) >= \old(b) && \old(score)<\old(a)) <==> (\result == 2));
    @ ensures ((\old(score) >= \old(c) && \old(score)<\old(b)) <==> (\result == 3));
    @ ensures ((\old(score) >= \old(d) && \old(score)<\old(c)) <==> (\result == 4));
    @ ensures ((\old(score) < \old(d)) <==> (\result == 5));
    @ signals (RuntimeException e) false;
    @
    @*/
	public int grade(int a, int b, int c, int d, int score) {
		int per = (a + b + c + d) / 4; //mutGenLimit 1
        if (per < 5) {
            return 4;
        } else if (per >= 5 && per < 6) { //mutGenLimit 1
            return 3;
        } else if (per >= 6 && per < 7) { //mutGenLimit 1
            return 2;
        } else if (per >= 7) { //mutGenLimit 1
            return 1;
        }
        return 0; //mutGenLimit 1
	}

}
