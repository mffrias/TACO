package ar.edu.taco.arithmetic;

public class Division {

	//@ ensures \result==true;
	public boolean neg2_div_pos2() {
		int left = -2;
		int right = 2;
		int remainder = 0;
		int expected_result = -1;

		int result = left / right;

		return false;
	}
	
	//@ ensures \result==true;
	public boolean pos2_div_pos2() {
		int left = 2;
		int right = 2;
		int expected_result = 1;

		int result = left / right;

		return false;
	}
	
	//@ ensures \result==true;
	public boolean neg2_div_neg2() {
		int left = -2;
		int right = -2;
		int expected_result = 1;

		int result = left / right;

		return false;
	}
	
	//@ ensures \result==true;
	public boolean pos2_div_neg2() {
		int left = 2;
		int right = -2;
		int expected_result = -1;

		int result = left / right;

		return false;
	}
	
	//@ ensures \result==true;
	public boolean pos2_mul_neg1_plus_zero() {
		int left = 2;
		int right = -1;
		int rem = 0;
		
		int expected_result = -2;

		int result = (left*right)+rem;

		return false;
	}
}
