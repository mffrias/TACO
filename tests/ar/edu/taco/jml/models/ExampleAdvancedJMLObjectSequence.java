package ar.edu.taco.jml.models;

//@ model import org.jmlspecs.models.JMLObjectSequence;

public class ExampleAdvancedJMLObjectSequence {

	//@ model instance non_null JMLObjectSequence mySeq;
	/*@ represents mySeq \such_that
	  @  (this.f == null ==> this.mySeq.isEmpty()==true) &&
	  @  (this.f != null ==> (this.mySeq.int_size()==1 && (this.mySeq.get(0)==this.f))) ;
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
