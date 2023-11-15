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
	 @ requires i1 > 0;
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
		if (i1*i1 + i2*i2 == -1358)
			return true;
		else
			return false;
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

	//    public static void main (String[] args) {
	//        int i1 = 479772853;
	//        System.out.println(sqrtOfMinus7(i1));
	//        System.out.println(i1*i1);
	//     }


	public static void main (String[] args) {
	    int i1 = 605467611;
	    int i2 = 605467611;
	    System.out.println(i1*i1 + i2*i2);
	}
}
