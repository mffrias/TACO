package ar.edu.taco.exception;

public class SignalsFalse {

	
	//@ ensures \result==0;
	//@ signals (Exception ex) false;
	public int return_zero() {
		return 0;
	}
	
	//@ exceptional_behavior
	//@   assignable \nothing;
	//@   signals (IndexOutOfBoundsException ex) true;
	//@   signals_only IndexOutOfBoundsException;
	public void throw_new_indexoutofbounds() {
		throw new IndexOutOfBoundsException();
	}
}
