package escj.test46;
/**
 ** Test escjava's reasoning about member inner classes, part V
 **/


/*
 * Test that invariants work properly:
 */
class Outer_2 {
    int x;

    class Inner {
	//@ invariant Outer.this.x>0;

	Inner() { x = 3; }

	Inner(char y) { }    // failure to establish invariant

	void m() {
	    //@ assert x>0;
	}
    }
}
