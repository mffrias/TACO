package ar.edu.taco.sbp;

public class NoFields {

	//@ ensures \result==true;
	public static boolean showInstance(int y) {
		return y != 0;
	}
}
