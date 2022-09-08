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
	 @ requires i1 != 1 && i2 != 1 && i1 != -1 && i2 != -1;
	 @ ensures \result == false; 
	 @*/
	public static boolean aerodynamic1 (int i1, int i2){

		if (i1*i2 == 1)
			return true;
		else
			return false;

	}




	/*@ requires i2 != 0 && i3 != 0;
  @ ensures \result == true;
  @ signals (RuntimeException e) false;
  @*/
	public static boolean testDivZero(int i1, int i2, int i3){
		i2 = i1/(i2/i3);
		return true;
	}


	//    public static void main(String[] args) {
	//  	  int i1 = 2024637831;
	//  	    int i2 = -638121483;
	//  	    int i3 = 447570985;
	//  	    int i4 = 1176443549;
	//  	    int i5 = -139742561;
	//  	    
	//  	    System.out.println(i1*i2*i3*i4*i5); 
	//  	    
	//	}	



	/*@ requires i > 5;
	  @ ensures \result == true;
	  @ signals (RuntimeException e) false;
	  @*/
	public static boolean testArrayObject(int i){
		Landing[] l = new Landing[i];
		l[0] = new Landing();
		l[i] = new Landing();
		return l.length > 5;
	}



	//	public static void main(String[] args){
	//	System.out.println(b);
	//}








	public static void main(String[] args){
	        int i1 = Integer.MAX_VALUE;
	        int i2 = 1;
	        System.out.println(i1 + i2);
	        System.out.println(-2147483648 - Integer.MIN_VALUE);
//	 		System.out.println(aerodynamic1(i1, i2));
	}
		
		


		//					public static void main(String[] args) {
		//						int i1 = 3147787;
		//				        int i2 = 1878016931;					
		//				        int result = i1*i2;
		//						System.out.println(result);
		//					}

		//	public static void main(String[] args) {
		//		float i1 = 1f;
		//		float i2 = 0f;
		//		boolean b = Landing.testDivZero(i1, i2);
		//	}


	}
