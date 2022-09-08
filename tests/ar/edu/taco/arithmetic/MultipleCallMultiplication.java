package ar.edu.taco.arithmetic;

public class MultipleCallMultiplication {

	/**
	 * @Ensures false;
	 */
	public void entry_point() {
		int a1 = 10;
		int b1 = 5;
		int c1;
		c1 = multiply(a1,b1);
		
		int a2 = 1;
		int b2 = 2;
		int c2;
		c2 = multiply(a2,b2);
	}
	
	public int multiply(int left, int right) {
		int result;
		result= left*right;
		return result;
	}
	
}
