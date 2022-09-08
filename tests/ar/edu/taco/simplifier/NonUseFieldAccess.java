package ar.edu.taco.simplifier;

public class NonUseFieldAccess {

	/*@ nullable @*/ Object f;
	
	public NonUseFieldAccess() {
		f = null;
	}
	
	/**
	 * @Ensures false;
	 */
	public boolean non_use_field_access() {
		return true;
	}
}
