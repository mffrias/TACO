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

	public static void main(String[] args) {
        int i1 = -76543212;
        int i2 = -668466018;
     boolean b = aerodynamic1(i1, i2);
     System.out.println(b);
     System.out.println(i1*i1 + i2*i2);
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
	 @ requires true;
	 @ ensures \result == false; 
	 @*/
	public static boolean aerodynamic1 (int i1, int i2){

		if (i1*i1 + i2*i2 == -76543212)
			return true;
		else
			return false;

	}

















		
		




	}
