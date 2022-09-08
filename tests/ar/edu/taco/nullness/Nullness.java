package ar.edu.taco.nullness;

public class Nullness {

	private Object f;

	private void m() {}
	
	/**
	  * @Requires n==null;
	  * @Ensures throw==null;
	  */
	public Object field_access(/*@ nullable @*/ Nullness n) {
		Object ret_val = n.f;
		return this;
	}
	
	/**
	  * @Requires n==null;
	  * @Ensures throw==null;
	  */
	public Object method_call(/*@ nullable @*/ Nullness n) {
		n.m();
		return this;
	}
	
}
