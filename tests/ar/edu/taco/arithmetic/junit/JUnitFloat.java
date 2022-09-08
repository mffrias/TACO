package ar.edu.taco.arithmetic.junit;

public class JUnitFloat {

	/**
	 * @Ensures return==true;
	 */
	public boolean positive(float x) {
		boolean result;
		if (x==1.1f)
			result = false;
		else
			result = true;
		return result;
	}
	
	/**
	 * @Ensures return==true;
	 */
	public boolean negative(float x) {
		boolean result;
		if (x==-1.1f)
			result = false;
		else
			result = true;
		return result;
	}
}
