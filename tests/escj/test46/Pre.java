package escj.test46;

/**
 ** Test escjava's reasoning about member inner classes, part I
 **/

/*
 * Verify that preconditions on enclosing instance variables work properly.
 */
class Outer_5 {
	int x;

	// Call from naked new:
	Inner test1() {
		return new Inner(); // error: x unknown
	}

	Inner test2() {
		x = 10;
		return new Inner(); // ok
	}

	// Call from new with explicit instance expression:
	Inner test3() {
		x = 10;
		Outer_5 o = new Outer_5();

		return o.new Inner(); // error: o.x unknown
	}

	Inner test4() {
		x = -10;
		Outer_5 o = new Outer_5();
		o.x = 10;

		return o.new Inner(); // ok
	}

	// Call from a sibling constructor:
	class Inner {
		//@ requires x>0;
		Inner() {
			//@ assert x>0;
		}

		//@ requires x>0;
		void requiresX() {
			//@ assert x>0;
		}

		Inner(int y) {
			this(); // error: x unknown
		}

		//@ requires x > 10;
		Inner(char y) {
			this(); // ok
		}
	}

	// Call via super() from a subclass...
	class SubInner1 extends Inner {
		SubInner1(int y) {
			super(); // error: x unknown
		}

		//@ requires x>2;
		SubInner1(char y) {
			super(); // ok
		}
	}

	// Call via E.super() from a subclass...
	class SubInner2 extends Inner {
		SubInner2(/*@ non_null @*/Outer_5 O) {
			O.super(); // error: O.x unknown
		}

		//@ requires O.x>2
		SubInner2(/*@ non_null @*/Outer_5 O, int x) {
			O.super(); // ok
		}
	}

	// Call an instance method via an Object:
	void testInstance() {
		x = 10;
		Inner I = new Inner();
		I.requiresX(); // no error!
		x = -10;
		I.requiresX(); // error
	}
}
