package ase2016.introclass.grade;

public class introclass_6e464f2b_000 {

	public introclass_6e464f2b_000() {
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
		int g;
		if (score < d) { //mutGenLimit 1
            return 5;
        } else if (score < c) { //mutGenLimit 1
            g = 3;
        } else if (score < b) { //mutGenLimit 1
            g = 2;
        } else {
            g = 1;
        }
        if (g == 1 || g == 2 || g == 3) {
            return g;
        }
        return 0; //mutGenLimit 1
	}

}
