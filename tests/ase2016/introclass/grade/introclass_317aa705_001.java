package ase2016.introclass.grade;

public class introclass_317aa705_001 {

	public introclass_317aa705_001() {
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
		int grade = 0; //mutGenLimit 1
		if (score >= a) { //mutGenLimit 1
            grade = 1;
        }
        if ((score < a) && (score >= b)) { //mutGenLimit 1
            grade = 2;
        }
        if ((score < b) && (score >= c)) { //mutGenLimit 1
            grade = 3;
        }
        if ((score < c) && (score >= d)) { //mutGenLimit 1
            grade = 4;
        }
        if (score < d) { //mutGenLimit 1
            grade = 5;
        }
        return grade;
	}

}
