package ar.edu.taco.simplifier;

public class AssignableField {
 
	/*@ nullable @*/ Object f;
	
	/*@
	  @ assignable f;
	  @ ensures this.f==null;
	  @*/
	 void assignable_f(/*@ non_null @*/ Object new_f) {
		f = new_f;
	}
}
