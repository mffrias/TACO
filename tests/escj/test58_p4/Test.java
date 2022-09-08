package escj.test58_p4;

/**
 ** This file tests that FindContributors includes the initialization
 ** of ghost fields in superinterfaces properly.
 **/

class Test {
	int k;

	Test() {
	}
}

interface Intf {
	//@ public ghost instance int x;
	//@ invariant 0 < x;
}

class Sub extends Test implements Intf {
	Sub() {
		super();
	} // error

	Sub(int y) {
		//@ set x=3;
	}
}

class SubSub extends Sub implements Intf {
	SubSub() {
		super(3);
	} // no error -- x is initialized only once
}
