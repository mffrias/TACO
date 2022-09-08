package ar.edu.taco.arrays;

public class Arrays {

	/*@
	  @ assignable \everything;
	  @
	  @ ensures \result==true;
	  @*/
	public boolean int_array_initialization() {
		int[] my_array = new int[4];
		if (my_array[0]==0)
			return true;
		else
			return false;
	}
}
