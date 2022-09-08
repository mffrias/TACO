package ar.edu.taco.jml.models;

//@ model import org.jmlspecs.models.JMLObjectSequence;

public class ExampleJMLObjectSequence {

	//@ model instance non_null JMLObjectSequence mySeq;
	/*@ represents mySeq \such_that
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
