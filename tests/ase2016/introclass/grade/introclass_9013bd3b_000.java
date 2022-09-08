package ase2016.introclass.grade;

public class introclass_9013bd3b_000 {

	public introclass_9013bd3b_000() {
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
		if (score >= a) { //mutGenLimit 1
            return 1;
        }
        if (score >= b && score < a) { //mutGenLimit 1
            return 2;
        }
        if (score >= c && score < b) { //mutGenLimit 1
            return 3;
        }
        if (score >= d && score < c) { //mutGenLimit 1
            return 4;
        }
        if (score < d) { //mutGenLimit 1
            return 5;
        }
        return 0; //mutGenLimit 1
	}

}
