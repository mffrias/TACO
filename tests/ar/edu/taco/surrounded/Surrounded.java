package ar.edu.taco.surrounded;

public class Surrounded {
	
    /*@ normal_behavior
    @   ensures \result < 0;
    @*/
	public /*@ pure @*/ int getAPositiveNumber() {
		int retValue = 1;
		
		try {
			boolean booleanValue = getBooleanValue();
		} catch (IllegalArgumentException e) {
			retValue = -1;
		}
		
		return retValue;
	}
	
    /*@ normal_behavior
    @   ensures \result < 0;
    @ also
    @ exceptional_behavior
    @   signals_only IllegalArgumentException;
    @*/	
	public /*@ pure @*/ int getANegativeNumber() {
		int retValue = -1;
		
		boolean booleanValue = getBooleanValue();
		retValue = -1;
		
		return retValue;
	}
	
	
	/*@
	@	requires true; 
	@*/
	public /*@ pure @*/ boolean getBooleanValue() throws IllegalArgumentException {
		throw new IllegalArgumentException(); 
	}
}
