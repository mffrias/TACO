package ar.edu.taco.arithmetic;

public class DivRem {

	//@ ensures \result==true;
	public boolean pos_div_pos() {
		int left = 7;
		int right = 3;
		int expected_result = 2;
		
		int result = left /right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}

	//@ ensures \result==true;
	public boolean neg_div_neg() {
		int left = -7;
		int right = -3;
		int expected_result = 2;
		
		int result = left /right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean neg_div_pos() {
		int left = -7;
		int right = 3;
		int expected_result = -2;
		
		int result = left /right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}	
	//@ ensures \result==true;
	public boolean pos_rem_pos() {
		int left = 7;
		int right = 3;
		int expected_result = 1;
		
		int result = left % right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean neg_rem_neg() {
		int left = -7;
		int right = -3;
		int expected_result = -1;
		
		int result = left % right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean neg_rem_pos() {
		int left = -7;
		int right = 3;
		int expected_result = -1;
		
		int result = left % right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}	

	//@ ensures \result==true;
	public boolean pos_rem_neg() {
		int left = 7;
		int right = -3;
		int expected_result = 1;
		
		int result = left % right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}
	
	//@ ensures \result==true;
	public boolean pos_div_neg() {
		int left = 7;
		int right = -3;
		int expected_result = -2;
		
		int result = left / right;
		
		boolean ret_val = expected_result != result;
		
		return ret_val;
	}
	

}
