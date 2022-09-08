package ar.edu.taco.jml.models;

//@ model import org.jmlspecs.models.JMLObjectSet;

public class ExampleJMLObjectSet {

	//@ model instance non_null JMLObjectSet mySet;
	/*@ represents mySet \such_that
	  @  (this.f==null) ;
	  @*/

	public /*@ nullable @*/ Object f;
	
	//@ ensures \result==true;
	public boolean showInstance() {
		boolean result = true; 
		if (this.f==null)
			result = false;
		
		return result;
	}
	
}
