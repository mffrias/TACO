package ar.edu.taco.sbp;

public class DataHeadTailList {

	public static class Data {
		
	}
	public static class Node {
		public /*@ nullable @*/ Node next;
		public /*@ nullable @*/ Node previous;
		public /*@ nullable @*/ Data value;
	}
	
	public /*@ nullable @*/ Node header;

	public /*@ nullable @*/ Node tail;

	//@ ensures \result==true;
	public boolean showInstance() {

		if (this.header==null)
			return true;

		if (this.tail==null)
			return true;
		
		if (this.header.next==null)
			return true;
		
		if (this.header.previous==null)
			return true;
	
		if (this.header.value==null)
			return true;
		
		return false;
	}

	//@ ensures \result==true;
	public static boolean showStaticInstance(DataHeadTailList entry) {

		if (entry==null) 
			return true;
		
		return entry.showInstance();
	}	
	
}
