package escj.test46;

/**
 ** Test escjava's reasoning about member inner classes, part II
 **/

/*
 * Verify that postconditions on enclosing instance variables work properly.
 */
class OuterPost {
	int x;

	// Call from naked new:
	void test1() {
		Inner I = new Inner();
		I.requireXPos();
	}

	// Call from new with explicit instance expression:
	void test3() {
		x = 10;
		OuterPost o = new OuterPost();

		Inner I = o.new Inner();
		//@ assert o.x>0;
	}

	// Verify postcondition is checked:
	class Inner {
		//@ ensures x>0;
		Inner() {
			x = 1;
		}

		//@ ensures x>0;
		Inner(char y) {
		} // error: fail because x not set

		//@ requires x>0
		void requireXPos() {
		}

		//@ ensures x>0
		void ensuresXPos() {
			x = 1;
		}

		// Call from a sibling constructor:
		//@ ensures x>0
		Inner(int y) {
			this();
			//@ assert x>0
		}
	}

	// Call via super() from a subclass...
	class SubInner1 extends Inner {
		SubInner1(int y) {
			super();
			super.requireXPos();
		}
	}

	// Call via E.super() from a subclass...
	class SubInner2 extends Inner {
		//@ ensures O.x>0;
		SubInner2(/*@ non_null @*/OuterPost O) {
			O.super();
			//@ assert O.x>0;
		}
	}

	// Call an instance method via an Object:
	void testInstance() {
		x = -10;
		Inner I = new Inner();
		I.ensuresXPos();
		//@ assert x>0
	}
}
