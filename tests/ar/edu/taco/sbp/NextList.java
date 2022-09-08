package ar.edu.taco.sbp;

public class NextList {

	public static class Node {
		public /*@ nullable @*/ Node next;
	}
	
	public /*@ nullable @*/ Node header;
	
	//@ ensures \result==true;
	public boolean showInstance() {
		if (this.header==null)
			return true;
		
		if (this.header.next==null)
			return true;
		
		return false;
	}
	
}
