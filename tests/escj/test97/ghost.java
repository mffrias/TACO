package escj.test97;

class Ghost97 {
	//@ ghost public int[] i;
	//@ghost public boolean b;

	//@invariant b && (b == true) && (i[0] == 0);

	Ghost97() {
		int ok[] = { 0, 1 };
		int bad[] = { 1, 0 };
		//@set i= ok; 
		//@set b= true;
		if (this.m()) {
			//@set i= ok;
		} else {
			//@set i= bad;
		}
	}

	//@ requires true;
	//@ ensures \result == this.b;
	boolean m() {
		return false;
	} //@nowarn Post;

}
