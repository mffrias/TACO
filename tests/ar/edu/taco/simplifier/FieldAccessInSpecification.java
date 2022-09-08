package ar.edu.taco.simplifier;

public class FieldAccessInSpecification {

	public Object f;

	//@ invariant this.f!=null;
	
	//@ ensures \result==true;
	public boolean spec_field_access() {
		boolean ret_val;
		if (this.f==null)
			ret_val = false;
		else 
			ret_val = true;
		
		return ret_val;
	}



}
