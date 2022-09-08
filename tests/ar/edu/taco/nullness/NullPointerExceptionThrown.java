package ar.edu.taco.nullness;

public class NullPointerExceptionThrown {

	/*@ nullable @*/ NullPointerExceptionThrown f;
	
	//@ signals (Exception ex) false;
	public void nullPointerExceptionIsThrown() {
		NullPointerExceptionThrown my_object = this.f.f;
		
		if(my_object==null)
			this.f = null;
		else
			my_object.f = this;
		
	}
	
}
