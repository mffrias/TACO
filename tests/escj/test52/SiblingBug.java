package escj.test52;

class SiblingBug {

	//@ invariant x>0;
	int x;

	SiblingBug() {
		x = 3;
	}

	SiblingBug(int r) {
		this();
	}

	SiblingBug(char r) {
		this();
		//@ assert x>0;
	}

	SiblingBug(boolean b) {
		this();

		x = x;

		//@ assert x>0;
	}

	static void m() {
		SiblingBug sb = new SiblingBug();
		//@ assert sb.x > 0;
	}

	SiblingBug(double d) {
		this();
		x--; // bad idea
	} // invariant doesn't necessarily hold here

	static void p() {
		SiblingBug sb = new SiblingBug();
		sb.x = 0; // bad idea
	} // invariant doesn't necessarily hold here
}
