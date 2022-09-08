package ase2016.introclass.grade;

public class introclass_fe9d5fb9_004 {

	public introclass_fe9d5fb9_004() {
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
		if (score >= a) {
            return 1;
        } else if (score >= b && score < a) {
            return 2;
        } else if (score >= c && score < b) {
            return 3;
        } else if (score >= d && score < c) {
            return 4;
        } else {
            return 5;
        }
	}

}
