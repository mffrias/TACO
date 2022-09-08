package ar.edu.taco.simplifier;

public class MultipleAssignments {

	private MultipleAssignments f;

	/*@
	  @ ensures false;
	  @*/
	public void multiple_assignments(MultipleAssignments o1, MultipleAssignments o2, MultipleAssignments o3) {
		o1.f = o2.f = o3.f = null;
	}
}
