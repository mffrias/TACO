package ase2016.introclass.grade;

public class introclass_0cea42f9_000 {

	public introclass_0cea42f9_000() {
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
        if (a > b && b > c && c > d) {
            if (score >= a) {
            	return 1;
            } else if (score < a && score >= b) {
            	return 2;
            } else if (score < b && score >= c) {
            	return 3;
            } else if (score < c && score >= d) {
            	return 4;
            } else if (score < d) {
            	return 5;
            } else {
            	return -1;
            }
        }
        return 0;
	}

}
