package ar.edu.taco.jml.models;

//@ model import org.jmlspecs.models.JMLObjectSet;

public class ExampleAdvancedJMLObjectSet {

	//@ model instance non_null JMLObjectSet mySet;
	/*@ represents mySet \such_that
	  @  (this.f == null ==> this.mySet.isEmpty()==true) &&
	  @  (this.f != null ==> (this.mySet.int_size()==1 && (this.mySet.has(this.f)==true))) ;
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
