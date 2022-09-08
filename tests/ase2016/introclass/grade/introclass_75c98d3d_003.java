package ase2016.introclass.grade;

public class introclass_75c98d3d_003 {

	public introclass_75c98d3d_003() {
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
		if (score < 4) { //mutGenLimit 1
            return 5;
        } else if (score < 5) { //mutGenLimit 1
            return 4;
        } else if (score < 6) { //mutGenLimit 1
            return 3;
        } else if (score < 7) { //mutGenLimit 1
            return 2;
        } else {
            return 1; //mutGenLimit 1
        }
	}

}
