package ar.edu.taco.sbp;


public class ListWithArray {

	public /*@ nullable @*/ Node header;
	
	public static class Node {
		public /*@ nullable @*/ Node next;
		public /*@ nullable @*/ Node previous;
	}
	
	//@ ensures \result==true;
	public boolean showInstance() {
		if (header!=null)
			return true;
		
		int[] my_int_array = new int[2];
		my_int_array[1] = 2;
		
		return false;
	}
}
