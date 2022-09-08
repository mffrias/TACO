package ar.edu.taco.infer;

public class PScopeNewObject {

	public PScopeNewObject() {}
	
	//@ ensures \result==true;
	public boolean create_two_objects() {
		
		Object o1 = new Object();
		Object o2 = new Object();
		PScopeNewObject pscope = new PScopeNewObject();

		return false;
	}
}
