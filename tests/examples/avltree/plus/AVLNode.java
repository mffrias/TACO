package examples.avltree.plus;

//TODO: check if the attributes should be public

public class AVLNode {

	public int element; // The data in the node

	public /*@ nullable @*/AVLNode left; 

	public /*@ nullable @*/AVLNode right;
	
	public /*@ nullable @*/AVLNode parent;

	public int height; 
	
	public int balance;


	public AVLNode () {}
	
}
