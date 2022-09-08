package escj.test46;

/**
 ** Test escjava's reasoning about member inner classes, part III
 **/

/*
 * Test modifies clauses:
 */
class Outer3 {
	int x;

	class Inner {
		Inner() {
		}

		//@ modifies x;
		Inner(int y) {
		}

		//@ modifies x;
		void modify() {
			x = 10;
		}
	}

	void test() {
		x = 3;
		Inner I = new Inner();
		//@ assert x>0;
		I.modify();
		//@ assert x>0 ;        // error
	}

	void test2() {
		x = 3;
		Inner I = new Inner(3);
		//@ assert x>0;         // error
	}
}
