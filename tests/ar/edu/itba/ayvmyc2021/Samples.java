package ar.edu.itba.ayvmyc2021;

public class Samples {

	/*@ requires input1 >= 0f;
	  @ requires input2 >= 0f;
	  @ ensures \result >= 0f;
	  @*/
	public static float sample(float input1, float input2){
		return input1 / input2;
	}
	
	public static void main(String[] args) {
        float input1 = Float.POSITIVE_INFINITY;
        float input2 = Float.POSITIVE_INFINITY;
        System.out.println(sample(input1,input2));
	}
	
}
