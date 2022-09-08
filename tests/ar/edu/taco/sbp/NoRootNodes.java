package ar.edu.taco.sbp;

public class NoRootNodes {

	private NoRootNodes f;

	//@ ensures \result==true;
	public static boolean showInstance() {
		int y = 0;
		return y != 0;
	}
}
