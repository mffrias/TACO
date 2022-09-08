package escj.test102;

// @see Ticket #28 : NullPointerException thrown with @pure modified comes before @modifies spec

public class C {

	//@ modifies \nothing;
	public/*@pure*/void foo() {
	}

	//@requires true;
	public void bar() {
		foo();
	}
}
