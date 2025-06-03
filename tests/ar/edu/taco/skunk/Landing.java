package ar.edu.taco.skunk;


public class Landing {


	//@requires a > 0 && b > 0 && a != Float.POSITIVE_INFINITY && b != Float.POSITIVE_INFINITY;
	//@ensures \result == false;
	public static boolean average(float a, float b){
		if (a + b == Float.NaN){
			return true;
		} else {
			return false;
		}
	}


	/*@ requires true;
	  @ ensures true;
	  @ signals (Exception e) false;
	  @*/
	static boolean initLandingOK(float height){

		Boolean b = isHeightPositive(height);

		return b.booleanValue();
	}


	static Boolean isHeightPositive(float height){
		Boolean b = null;
		if (height > 0)
			b = Boolean.TRUE;

		if (height <= 0)
			b = Boolean.FALSE;

		return b;
	}

//	public static void  main(String[] args){
//		float a = 5.108806E30f;
//		float b = 15.9375f;
//		System.out.println(a + b);
//	}


	/*@
	 @ requires true;
	 @ ensures \result == false; 
	 @*/
	public static boolean sqrtOfMinus7 (int i1){
		if (i1*i1 ==  -7)
			return true;
		else
			return false;
	}



	/*@
	 @ requires i2 != 0;
	 @ ensures \result == false; 
	 @*/
	public static boolean aerodynamic1 (int i1, int i2){
		if (i1*i1 + i2*i2 == -1358)
			return true;
		else
			return false;
	}


	/*@
	 @ requires factor1 > 1 && factor2 > 1 && factor1 < i && factor2 < i && i == 573193L;
	 @ ensures \result == false;
	 @*/
	public static boolean getFactor(long i, long factor1, long factor2) {
		if (factor1 * factor2 == i)
			return true;
		else
			return false;

	}

//	    public static void main (String[] args) {
//			int i1 = -2062044091;
//			int i2 = -459124741;
//			System.out.println(aerodynamic1(i1, i2));
//	        System.out.println(i1*i1 + i2*i2);
//	     }

    /*@ requires true;
         ensures true;
         signals (Exception e) false;
     @*/
	int sum(int limit)
	{
		int[] array = new int[10];
		int i, sum;

		sum = 0;
		for(i = 0; i < limit; i++){
			array[i] = i;
		}

		return sum;
	}


	public static void main (String[] args) {
		int i1 = 869476073;
		System.out.println(i1 * i1);

	}
}
