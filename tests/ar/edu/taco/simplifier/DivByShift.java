package ar.edu.taco.simplifier;

public class DivByShift {

	//@ ensures \result==0;
	public int m1(int i) {
		int j = i / 2;
		return j;
	}
}
