package ar.edu.taco.arithmetic;

public class WhileMult {

	/**
	 * @Ensures return==7;
	 */
	public int m() {
		int fake = 7;
		int result = 8;
		int i = 1;
		while (i<=4) {
			i = i * 2;
		}
		return i;
	}

}
