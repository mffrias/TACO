package ar.edu.taco.simplifier;

public class NullableAnnotation {

	public/*@ nullable @*/Object field;

	/*@
	  @ ensures \result==true;
	  @*/
	public boolean nullable_argument(/*@ nullable @*/ Object argument) {
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
	public /*@ nullable @*/ Object nullable_return_value(Object argument) {
		Object ret_val = null;
		return ret_val;
	}
	
	
}
