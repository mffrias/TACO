package ar.edu.taco.sbp;

public class DataLeftRightParentTree {

	public static class Data {
		
	}
	
	public static class Node {
		public /*@ nullable @*/ Node left;
		public /*@ nullable @*/ Node right;
		public /*@ nullable @*/ Node parent;
		public /*@ nullable @*/ Data value;
	}
	
	public /*@ nullable @*/ Node root;
	
	//@ ensures \result==true;
	public boolean showInstance() {

		if (this.root==null)
			return true;
		
		if (this.root.left==null)
			return true;
		
		if (this.root.right==null)
			return true;

		if (this.root.parent==null)
			return true;

		if (this.root.value==null)
			return true;
		
		return false;
	}
	
}
