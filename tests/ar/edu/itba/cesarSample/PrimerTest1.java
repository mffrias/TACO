package ar.edu.itba.cesarSample;

public class PrimerTest1 {
	
	
	
  /*@ requires f >= 0;
    @ ensures \result >= 0;
	@*/
	public static float sample(float f){
		return f*f;
	}
	
/*	public static void main(String[] args) {
        int i = 17;
        int j = -252645135;

	        System.out.println(sample(i,j));
	 
	}
*/
}
