package ar.edu.taco.simplifier;

public class NonNullAnnotation {

	public/*@ non_null @*/Object field;

	/*@
	  @ ensures \result==true;
	  @*/
	public boolean non_null_argument(final /*@ non_null @*/ Object argument) {
		boolean ret_val;
		if (argument != null) {
			ret_val = true;
		} else {
			ret_val = false;
		}
		return ret_val;
	}
	
	/*@
	  @ ensures \result!=null;
	  @*/
	public /*@ non_null @*/ Object non_null_return_value(Object argument) {
		Object ret_val = null;
		return ret_val;
	}
	
	
}
