package ar.edu.unrc.cacic2019;

public class ArithExample {
	
	
	
	/*@
	 @ requires i1 > 0 && i2 > 0 && i3 > 0 && i4 > 0 && i5 > 0;
	 @ ensures \result == false; 
	 @*/
	public static boolean condNoTrivial(long i1, long i2, long i3, long i4, long i5){
		if (i1*i1 + i2*i2 + i3*i3 + i4*i4 + i5*i5 == 0)
			return true;
		else
			return false;
	}
	
		
	
	
	
	
	
	

}
