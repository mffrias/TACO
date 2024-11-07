package ar.edu.taco.skunk;


public class Landing {

	static Boolean isHeightPositive(float height){
		Boolean b = null;
		if (height > 0)
			b = Boolean.TRUE;


		if (height <= 0)
			b = Boolean.FALSE;

		return b;
	}





	/*@ requires true;
	  @ ensures true;
	  @ signals (Exception e) false;
	  @*/
	static boolean initLandingOK(float height){

		Boolean b = isHeightPositive(height);

		return b.booleanValue();
	}




	/*@
	 @ requires i1 != 479772853;
	 @ ensures \result == false; 
	 @*/
	public static boolean sqrtOfMinus7 (int i1){
		if (i1*i1 == -7)
			return true;
		else
			return false;
	}



	/*@
	 @ requires true;
	 @ ensures \result == false; 
	 @*/
	public static boolean aerodynamic1 (int i1, int i2){
		if (i1*i1 + i2*i2 == -127)
			return true;
		else
			return false;
	}

	/*@ requires f1 != 0f && f2 != 0f;
	  @ ensures \result == false || f1 == Float.NaN || f2 == Float.NaN;
	  @*/
	public static boolean addFloats(float f1, float f2){
		if (f1 + f2 == f1)
			return true;
		else
			return true;
	}


	/*@
	 @ requires factor1 > 1 && factor2 > 1 && factor1 < i && factor2 < i && i == 107151;
	 @ ensures \result == false;
	 @*/
	public static boolean getFactor(int i, int factor1, int factor2) {
		if (factor1 * factor2 == i)
			return true;
		else
			return false;

	}

	/*@ requires array.length == 10;
	  @ ensures true;
	  @ signals (Exception e) false;
	  @*/
	public static int sum(int[] array)
	{
		int sum = 0;
		for(int i = 0; i < 10; i++) {
			sum += array[i];
			if (i == 9) {
				sum += array[10];
			}
		}

		return sum;
	}

}
