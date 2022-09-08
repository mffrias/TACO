package ar.edu.taco.jml.models;

//@ model import org.jmlspecs.models.JMLObjectSet;

public class ExampleJMLReach {


	/*@
	  @ invariant \reach(this.f, ExampleJMLReach, f).has(this)==true;
	  @*/
	
	public /*@ nullable @*/ ExampleJMLReach f;
	
	//@ ensures \result==true;
	public boolean showInstance() {
		boolean result = false; 
		
		return result;
	}
	
}
