package  ar.edu.taco.skunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;




public class TreeSet {
	
	 public final int RED = 0;
	 public final int BLACK = 1;



	//Authors: Marcelo Frias
	/*@
	  @ invariant ( this.RED==0 ) && 
	  @		( this.BLACK==1 ) &&
	  @		( this.root.parent == null );
	  @*/
	
	/*@
	  @ requires true;
	  @ ensures false;
	  @*/
	public void generateInput(){
		
	}
	
	//*************************************************************************
	//************************* From now on repOk  ****************************
	//*************************************************************************.

	
	public /*@ nullable @*/ TreeSetEntry root = null;

	/**
	 * The number of entries in the tree
	 */
	public  int size = 0;

	/**
	 * The number of structural modifications to the tree.
	 */

	
//	public TreeSet(){}
	
	public TreeSet(TreeSetEntry r, char c){
		root =r;
		size =1;
	}

  
	
	
	
		
		






	/*
	 * Returns the successor of the specified Entry, or null if no such.
	 */
	
	
}
