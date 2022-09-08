package ar.edu.taco.simplifier;

public class MultipleSpecCases {

	/*@
	  @ normal_behavior
	  @   requires y!=0;
	  @   ensures \result==x;
	  @ also
	  @ exceptional_behavior
	  @   requires y==0;
	  @   signals_only RuntimeException;
	  @*/
	public /*@ pure @*/ int spec_cases(int x, int y) {
		if (y==0) {
			throw new RuntimeException();
		}
		return x;
	}
}
