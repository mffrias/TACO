package jason.jml.treeset;

public class Entry {

	public Entry(int key_param, Entry parent_param) {
		key = key_param;
		parent = parent_param;
	}

	/*@ spec_public @*/int key;
	
	boolean color; 
	
	/*@ nullable @*/ Entry left;
	
	/*@ nullable @*/ Entry right;
	
	/*@ nullable @*/ Entry parent;

	//@ model int blackHeight;
	
	/*@
	  @ represents blackHeight \such_that true;
	  @*/
}
