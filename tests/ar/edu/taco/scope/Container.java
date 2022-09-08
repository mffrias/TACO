package ar.edu.taco.scope;

public class Container {

	public /*@ nullable @*/ Node header;
	
	//@ ensures \result==true;
	public boolean show_instance() {
		if (header!=null)
			return false;
		
		if (header.next!=null)
			return false;
		
		if (header.next.next!=null)
			return false;
		
		return true;
	}

}
