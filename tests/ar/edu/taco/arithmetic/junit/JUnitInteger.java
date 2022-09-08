package ar.edu.taco.arithmetic.junit;

public class JUnitInteger {
	
	/**
	 * @Ensures return==true;
	 */
	public boolean positive(int x) {
		boolean result;
		if (x>0)
			result = false;
		else
			result = true;
		return result;
	}

	/**
	 * @Ensures return==true;
	 */
	public boolean negative(int x) {
		boolean result;
		if (x<0)
			result = false;
		else
			result = true;
		return result;
	}

}
