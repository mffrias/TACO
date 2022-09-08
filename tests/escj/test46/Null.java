package escj.test46;

/**
 ** Verify that we do null checks for the enclosing instance arguments
 ** of new and super.
 **/

class Outer_4 {
	class Inner {
	}

	void test1(Outer_4 O) {
		Inner I = O.new Inner(); // null error
	}

	class Lower extends Inner {
		Lower(Outer_4 O) {
			O.super(); // null error
		}
	}
}
