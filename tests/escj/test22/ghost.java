package escj.test22;

class Ghost22 {
	//@ ghost public int i;
	//@ ghost public boolean b;

	// @invariant b && (b == true) && (i > 0);

	Ghost22() {
		//@ set i= 6; 
		//@ set b= true;
		if (this.m()) {
			//@ set i= 6;
		} else {
			//@ set i= -10;
		}
	}

	//@ requires true;
	//@ ensures \result == this.b;
	boolean m() {
		return false;
	} //@ nowarn Post;

}
