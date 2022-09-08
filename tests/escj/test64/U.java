package escj.test64;

class U {
	int x;
	//@ invariant 10 <= x;
	int y;
	//@ invariant 10 <= y;
	int z;

	//@ invariant 10 <= z;

	U(int tx, int ty, int tz) {
		x = 10;
		y = 10;
		z = 10; // remove nondeterminism in which invariant is
		// complained about below
		x = tx;
		m(); // invariant warning about x when checked without inlining
		y = ty;
		n(); // invariant warning about y when checked without inlining
		z = tz;
		o(); // invariant warning about z when checked without inlining
	}

	final void m() {
		x = p();
	} // warning about x's invariant when checking m

	private int p() {
		return 12;
	}

	final void n() {
		y = q();
	}

	private/*@ helper */int q() {
		return 14;
	}

	final void o() {
		z = r(18);
	} // warning about z's invariant when checking o

	//@ helper
	private int r(int t) {
		return s(t);
	}

	private int s(int t) {
		return t;
	}

	U(double tx) {
		x = 10;
		y = 10; // remove nondeterminism in which invariant is
		// complained about below
		rec(tx); // invariant warning about z when checked without inlining
	}

	private void rec(double dx) {
		if (dx < 0.0) {
			x = 10;
			y = 10;
			z = 10;
		} else {
			rec(dx - 1.0);
		}
	}
}
