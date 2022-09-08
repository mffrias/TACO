package ar.edu.taco.arithmetic.junit;

public class JUnitLong {
	
	/**
	 * @Ensures return==true;
	 */
	public boolean positive(long x) {
		boolean result;
		if (x==3427915184372166657L)
			result = false;
		else
			result = true;
		return result;
	}

	/**
	 * @Ensures return==true;
	 */
	public boolean negative(long x) {
		boolean result;
		if (x==-35184372166657L)
			result = false;
		else
			result = true;
		return result;
	}

}
